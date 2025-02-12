package de.fpolachowski.papercurator.controller

import de.fpolachowski.papercurator.model.Author
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.service.DocumentService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class DocumentController(private val documentService: DocumentService) {

    @QueryMapping
    fun getDocuments(): MutableIterable<Document> {
        return documentService.findAll()
    }

    @QueryMapping
    fun getDocumentById(id: Long): Document? {
        return documentService.findById(id)
    }

    @QueryMapping
    fun findDocumentsByTitle(title: String): List<Document> {
        return documentService.findAllByTitle(title)
    }

    @SchemaMapping
    fun authors(doc : Document) : List<Author> {
        return doc.authors
    }
}