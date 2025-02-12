package de.fpolachowski.papercurator.repository

import de.fpolachowski.papercurator.model.Document
import org.springframework.data.repository.CrudRepository

interface DocumentRepository : CrudRepository<Document, Long> {
    fun findByTitle(title: String): Document?
    fun findAllByTitle(title: String): List<Document>
}