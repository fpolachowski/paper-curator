package de.fpolachowski.papercurator.etl

import de.fpolachowski.papercurator.etl.arxiv.PaperDocumentReader
import de.fpolachowski.papercurator.model.ContentType
import de.fpolachowski.papercurator.model.Document
import org.springframework.ai.document.Document as AIDocument
import org.springframework.ai.document.DocumentTransformer
import org.springframework.ai.document.DocumentWriter
import java.io.FileNotFoundException

class ETLPipeline(
    private val documentRetriever: DocumentLoader,
    private val documentReader: PaperDocumentReader,
    private val documentTransformer: DocumentTransformer,
    private val documentWriter: DocumentWriter
) {

    fun run(url : String) : List<Document> {
        val documents = documentRetriever.read(url)
        for (document in documents) {
            try {
                val linkURL = document.urls.find { it.contentType == ContentType.PDF } ?: continue
                val aiDocuments = documentReader.read(linkURL.url)
                val transformedDocuments = documentTransformer.apply(aiDocuments)

                // Insert metadata into [AIDocument] for filtering and association with source [Document]
                val finalDocuments = transformedDocuments.map { it.text?.let { text ->
                    AIDocument(
                        text,
                        mapOf("title" to document.title, "date" to document.date))
                } }

                documentWriter.accept(finalDocuments)
                Thread.sleep(500) //To not exceed the api threshold
            } catch (e: Exception) {
                when (e.cause) {
                    is FileNotFoundException -> continue //TODO(Logging)
                    else -> e.printStackTrace()
                }
            }
        }
        return documents
    }

}