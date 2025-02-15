package de.fpolachowski.papercurator.etl

import de.fpolachowski.papercurator.model.ContentType
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.util.StringManipulator.Companion.cleanString
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

                // Clean the string and remove duplicate spaces
                val finalDocuments = transformedDocuments.map { it.text?.let { text ->
                    AIDocument(
                        cleanString(text),
                        it.metadata
                    )
                } }

                documentWriter.accept(finalDocuments)
                document.documents = finalDocuments.mapNotNull { it?.id }
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