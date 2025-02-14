package de.fpolachowski.papercurator.etl.arxiv

import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import de.fpolachowski.papercurator.etl.DocumentLoader
import de.fpolachowski.papercurator.model.Author
import de.fpolachowski.papercurator.model.ContentType
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.model.Link
import de.fpolachowski.papercurator.util.StringManipulator.Companion.cleanString
import de.fpolachowski.papercurator.util.TimeManipulator.Companion.dateToLocalDateTime
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets

class AtomFeedParser : DocumentLoader {
    private val arxivContentTypeConverter = ArxivContentTypeConverter()

    private fun openFeed(url : URL): SyndFeed? {
        val inputStream = url.openStream()
        val reader = XmlReader(inputStream, StandardCharsets.UTF_8.name())
        val input = SyndFeedInput()
        return input.build(reader)
    }

    private fun createDocument(entry : SyndEntry) : Document? {
        val title = cleanString(entry.title)
        val updateDate = dateToLocalDateTime(entry.updatedDate)
        val authors = entry.authors.map { Author(null, it.name) }
        val categories = entry.categories.map { it.name }
        val urls = entry.links.filter { it.type != null }.map { Link(null, it.href, arxivContentTypeConverter.convert(it.type)) }.toMutableList()

        if (urls.find { it.contentType == ContentType.PDF } == null) {
            val websiteUrl = urls.find { it.contentType == ContentType.WEBSITE }?.url ?: return null
            urls.add(Link(null, websiteUrl.replace("abs", "pdf"), ContentType.PDF))
        }

        return Document(
            id = null,
            title = title,
            urls = urls,
            authors = authors,
            date = updateDate,
            categories = categories
        )
    }

    override fun read(url : String): List<Document> {
        val uri = URI(url).toURL()
        val feed = openFeed(uri)
        val entries = feed?.entries ?: return listOf()
        val documents = mutableListOf<Document>()
        for (entry in entries) {
            createDocument(entry)?.let { documents.add(it) }
        }
        return documents
    }
}