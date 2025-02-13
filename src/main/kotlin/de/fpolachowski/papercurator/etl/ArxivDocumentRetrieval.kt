package de.fpolachowski.papercurator.etl

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
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets


@Service
@EnableScheduling
class ArxivDocumentRetrieval(
    private val documentRepository: DocumentRepository
) : DocumentRetrieval {
    private val defaultSort = "lastUpdatedDate"
    private val defaultOrder = "ascending"
    private val extractedTextFormatter = ExtractedTextFormatter.builder().build()
    private val pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder().withPageExtractedTextFormatter(extractedTextFormatter).build()
    private val categories = listOf("cs")

//    @Scheduled(cron = "0 0 2 * * *", zone = "UTC") //TODO(include)
    @Scheduled(fixedDelay = 86400000L)
    override fun retrieveDailyDocuments() {
        for (category in categories) {
            val documents = findAllByCategory(category)
            for (document in parseDocuments(documents)) {
                documentRepository.save(document)
                Thread.sleep(500) //To not exceed the api threshold
            }
        }
    }

    override fun findAllByCategory(category: String): List<Document> {
        val url = URI("https://rss.arxiv.org/atom/$category").toURL()
        return createDocumentsFromAtom(url)
    }

    override fun findById(id: String): Document? {
        val url = URI("http://export.arxiv.org/api/query?search_query=id_list:${id}&sortBy=${this.defaultSort}&sortOrder=${this.defaultOrder}").toURL()
        return createDocumentsFromAtom(url).firstOrNull()
    }

    override fun findAllByTitle(title: String): List<Document> {
        val url = URI("http://export.arxiv.org/api/query?search_query=ti:${title}&sortBy=${this.defaultSort}&sortOrder=${this.defaultOrder}").toURL()
        return createDocumentsFromAtom(url)
    }

    /**
     * Parses individual documents in succession and yields them as an iterator.
     * This is done to minimize the memory footprint of this process as the
     * [Document.content] contains multiple pages of pdf content.
     * @return [Document] with updated content
     */
    private fun parseDocuments(documents : List<Document>) = iterator {
        for (document in documents.subList(0, 20)) { //TODO(remove limit)
            val updatedDocument = try {
                parseDocument(document)
            } catch (e: Exception) {
                when (e.cause) {
                    is FileNotFoundException -> continue //TODO(Logging)
                    else -> println(e.message) //TODO(Logging)
                }
                null
            }
            if (updatedDocument != null) {
                yield(updatedDocument)
            }
        }
    }

    private fun parseDocument(document : Document): Document? {
        val pdfUrl = document.urls.find { it.contentType == "text/html" } ?: return null
        val pdfReader = PagePdfDocumentReader(pdfUrl.url.replace("abs", "pdf"), pdfDocumentReaderConfig)
        val content = pdfReader.read().map { it.text?.let { text -> cleanString(text) } }.joinToString(separator = "")
        document.content = content
        return document
    }

    private fun openFeed(url : URL): SyndFeed? {
        val inputStream = url.openStream()
        val reader = XmlReader(inputStream, StandardCharsets.UTF_8.name())
        val input = SyndFeedInput()
        return input.build(reader)
    }

    private fun createDocumentsFromAtom(url : URL) : List<Document> {
        val feed = openFeed(url)
        val entries = feed?.entries ?: return listOf()
        val documents = mutableListOf<Document>()
        for (entry in entries) {
            val title = cleanString(entry.title)
            val updateDate = dateToLocalDateTime(entry.updatedDate)
            val urls = entry.links.filter { it.type != null }.map { Link(null, it.href, it.type) }
            val authors = entry.authors.map { Author(null, it.name) }
            val categories = entry.categories.map { it.name }

            documents.add(Document(
                id = null,
                title = title,
                urls = urls,
                authors = authors,
                date = updateDate,
                categories = categories
            ))
        }
        return documents
    }
}