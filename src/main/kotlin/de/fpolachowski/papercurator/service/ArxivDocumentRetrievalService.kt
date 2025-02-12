package de.fpolachowski.papercurator.service

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader

import de.fpolachowski.papercurator.model.Document
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets


@Service
class ArxivDocumentRetrievalService : DocumentRetrievalService {
    private val defaultSort = "lastUpdatedDate"
    private val defaultOrder = "ascending"
    private val baseUrl = "http://export.arxiv.org/api"

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        this.findByTitle("electron")
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
        for (entry in entries) {
            println("\nTitle: ${entry.title}".trimIndent())
            println("Updated: " + entry.updatedDate)
            println("Link: " + entry.links)
            println("Summary: " + entry.description)
            println("Authors: " + entry.authors)
            println("Categories: " + entry.categories)
        }
        return listOf()
    }
}