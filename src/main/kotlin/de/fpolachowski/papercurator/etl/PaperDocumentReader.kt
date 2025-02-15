package de.fpolachowski.papercurator.etl

import org.springframework.ai.document.Document

/**
 * Custom interface for [DocumentReader] to include the url in the function call
 */
interface PaperDocumentReader {
    fun read(url : String): List<Document> { return get(url) }
    fun get(url : String) : List<Document>
}