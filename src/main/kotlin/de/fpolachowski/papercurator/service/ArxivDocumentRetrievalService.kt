package de.fpolachowski.papercurator.service

import de.fpolachowski.papercurator.model.Document
import org.springframework.stereotype.Service
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document as DomDocument


@Service
class ArxivDocumentRetrievalService : DocumentRetrievalService {
    private val factory = DocumentBuilderFactory.newInstance()
    private val builder = factory.newDocumentBuilder()
    private val defaultSort = "lastUpdatedDate"
    private val defaultOrder = "ascending"

    override fun findAllByCategory(category: String): List<Document> {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Document? {
        TODO("Not yet implemented")
    }

    override fun findByTitle(title: String): List<Document> {
        val url = "http://export.arxiv.org/api/query?search_query=ti:\"${title}\"&sortBy=${this.defaultSort}&sortOrder=${this.defaultOrder}\n"
        val doc: DomDocument = builder.parse(url)

        doc.documentElement.normalize()

        val entries = doc.getElementsByTagName("entry")
        for (i in 0..<entries.length) {
            val node = entries.item(i)
            if (node.nodeType != Node.ELEMENT_NODE) {continue}
            val entry = node as Element
            val entryTitle = entry.getElementsByTagName("title").item(0).textContent
            if (entryTitle == "error") {continue} //TODO("Add Logging for errors")

        }
    }

}