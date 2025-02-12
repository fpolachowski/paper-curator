package de.fpolachowski.papercurator.service

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import de.fpolachowski.papercurator.model.Author

import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.repository.DocumentRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.ZoneId


@Service
class ArxivDocumentRetrievalService(
    private val documentRepository: DocumentRepository
) : DocumentRetrievalService {
    private val defaultSort = "lastUpdatedDate"
    private val defaultOrder = "ascending"
    private val baseUrl = "http://export.arxiv.org/api"

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        val documents = this.findByTitle("electron")
        documentRepository.saveAll(documents)
    }

    override fun findAllByCategory(category: String): List<Document> {
        return listOf()
    }

    override fun findById(id: String): Document? {
        return null
    }

    override fun findByTitle(title: String): List<Document> {
        val url = URI("${this.baseUrl}/query?search_query=ti:${title}&sortBy=${this.defaultSort}&sortOrder=${this.defaultOrder}").toURL()
        return parseDocuments(url)
    }

    fun parseDocuments(url : URL) : List<Document> {
        val inputStream = url.openStream()
        val reader = XmlReader(inputStream, StandardCharsets.UTF_8.name())
        val input = SyndFeedInput()
        val feed = input.build(reader)
        val entries = feed.entries

        val documents = mutableListOf<Document>()
        for (entry in entries) {
            val title = entry.title.trimIndent()
            val updateDate = entry.updatedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val links = entry.links.map { it.href }
            val authors = entry.authors.map { Author(null, it.name) }
            val categories = entry.categories.map { it.name }

            documents.add(Document(
                null,
                title,
                links,
                authors,
                "",
                "",
                "",
                updateDate,
                categories
            ))
        }
        return documents
    }
}