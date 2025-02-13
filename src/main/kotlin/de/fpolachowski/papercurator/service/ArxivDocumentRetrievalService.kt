package de.fpolachowski.papercurator.service

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import de.fpolachowski.papercurator.model.Author
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.model.Link
import de.fpolachowski.papercurator.repository.DocumentRepository
import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
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
        var documents = createDocuments(url)
        documents = parseDocuments(documents)
        return documents
    }

    fun parseDocuments(documents : List<Document>): List<Document> {
        for (document in documents) {
            val pdfUrl = document.urls.find { it.contentType == "application/pdf" } ?: continue
            try {
                val pdfReader = PagePdfDocumentReader(
                    pdfUrl.url,
                    PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(
                            ExtractedTextFormatter.builder().build()
                        ).build()
                )
                val content = pdfReader.read().toString()
                document.content = content
            } catch (e: Exception) {
                println(e.message)
                continue
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
            val title = entry.title.replace(Regex("""(\r\n)|\n|"""), "").replace(Regex("""\s+"""), " ")
            val updateDate = entry.updatedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
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