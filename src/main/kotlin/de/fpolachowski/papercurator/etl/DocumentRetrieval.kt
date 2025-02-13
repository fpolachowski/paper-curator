package de.fpolachowski.papercurator.etl

import de.fpolachowski.papercurator.model.Document

interface DocumentRetrieval {
    fun retrieveDailyDocuments()
    fun findAllByCategory(category: String): List<Document>
    fun findById(id: String): Document?
    fun findAllByTitle(title: String): List<Document>
}