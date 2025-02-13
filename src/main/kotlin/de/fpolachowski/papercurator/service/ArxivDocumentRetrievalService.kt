package de.fpolachowski.papercurator.service

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import de.fpolachowski.papercurator.model.Author
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.model.Link
import de.fpolachowski.papercurator.repository.DocumentRepository
import de.fpolachowski.papercurator.util.StringManipulator.Companion.cleanString
import de.fpolachowski.papercurator.util.TimeManipulator.Companion.dateToLocalDateTime
import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets


@Service
class ArxivDocumentRetrievalService(
    private val documentRepository: DocumentRepository
) : DocumentRetrievalService {
    private val defaultSort = "lastUpdatedDate"
    private val defaultOrder = "ascending"
    private val baseUrl = "http://export.arxiv.org/api"
    private val extractedTextFormatter = ExtractedTextFormatter.builder().build()
    private val pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder().withPageExtractedTextFormatter(extractedTextFormatter).build()

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
        var documents = createDocuments(url)
        documents = parseDocuments(documents)
        return documents.filter { it.content != "" }
    }

    fun parseDocuments(documents : List<Document>): List<Document> {
        for (document in documents) {
            val pdfUrl = document.urls.find { it.contentType == "application/pdf" } ?: continue
            try {
                val pdfReader = PagePdfDocumentReader(pdfUrl.url, pdfDocumentReaderConfig)
                val content = pdfReader.read().map { it.text?.let { it1 -> cleanString(it1) } }.joinToString(separator = "")
                document.content = content
            } catch (e: Exception) {
                when (e.cause) {
                    is FileNotFoundException -> continue //TODO(Logging)
                    else -> throw e
                }
            }
        }

        return documents
    }

    fun openFeed(url : URL): SyndFeed? {
        val inputStream = url.openStream()
        val reader = XmlReader(inputStream, StandardCharsets.UTF_8.name())
        val input = SyndFeedInput()
        return input.build(reader)
    }

    fun createDocuments(url : URL) : List<Document> {
        val feed = openFeed(url)
        val entries = feed?.entries ?: return listOf()

        val documents = mutableListOf<Document>()
        for (entry in entries) {
            val title = cleanString(entry.title)
            val updateDate = dateToLocalDateTime(entry.updatedDate)
            val links = entry.links.filter { it.type != null }.map { Link(null, it.href, it.type) }
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