package de.fpolachowski.papercurator.service

import de.fpolachowski.papercurator.model.Document

interface DocumentRetrievalService {
    fun findAllByCategory(category: String): List<Document>
    fun findById(id: String): Document?
    fun findByTitle(title: String): List<Document>
}