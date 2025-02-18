package de.fpolachowski.papercurator.etl

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.pgvector.PgVectorStore

/**
 * Custom function for retrieving [Document] from the [PgVectorStore] vector store based on their ids
 */
fun PgVectorStore.findById(ids : List<String>): List<Document> {
    return listOf()
}