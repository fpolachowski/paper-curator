package de.fpolachowski.papercurator.etl.arxiv

import de.fpolachowski.papercurator.etl.DocumentRetrieval
import de.fpolachowski.papercurator.etl.ETLPipeline
import de.fpolachowski.papercurator.etl.PdfDocumentReader
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.repository.DocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.pgvector.PgVectorStore
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
@EnableScheduling
class ArxivDocumentRetrieval(
    private val documentRepository: DocumentRepository,
    private val vectorStore: PgVectorStore
) : DocumentRetrieval {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val defaultSort = "lastUpdatedDate"
    private val defaultOrder = "ascending"
    private val categories = listOf("cs")
    private val pipeline = ETLPipeline(
        AtomFeedParser(),
        PdfDocumentReader(),
        TokenTextSplitter(1000, 400, 10, 5000, false),
        vectorStore
    )

//    @Scheduled(cron = "0 0 2 * * *", zone = "UTC") //TODO(include)
    @Scheduled(fixedDelay = 86400000L, initialDelay = 0)
    override fun retrieveDailyDocuments() {
        //for (category in categories) {
        //    val documents = findAllByCategory(category)
        //    documentRepository.saveAll(documents)
        //}
        logger.info("Retrieving daily documents")

        val documents = findAllByTitle("electron")
        documentRepository.saveAll(documents)
        logger.info("Retrieved ${documents.size} documents")
    }

    override fun findAllByCategory(category: String): List<Document> {
        logger.info("Processing category: $category")
        val url = "https://rss.arxiv.org/atom/$category"
        return pipeline.run(url)
    }

    override fun findById(id: String): Document? {
        logger.info("Processing document by id: $id")
        val url = "http://export.arxiv.org/api/query?search_query=id_list:${id}&sortBy=${this.defaultSort}&sortOrder=${this.defaultOrder}"
        return pipeline.run(url).firstOrNull()
    }

    override fun findAllByTitle(title: String): List<Document> {
        logger.info("Processing document by title: $title")
        val url = "http://export.arxiv.org/api/query?search_query=ti:${title}&sortBy=${this.defaultSort}&sortOrder=${this.defaultOrder}"
        return pipeline.run(url)
    }
}