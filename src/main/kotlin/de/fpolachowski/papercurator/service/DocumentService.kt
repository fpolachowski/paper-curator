package de.fpolachowski.papercurator.service

import de.fpolachowski.papercurator.model.Author
import de.fpolachowski.papercurator.model.Document
import de.fpolachowski.papercurator.repository.AuthorRepository
import de.fpolachowski.papercurator.repository.DocumentRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val authorRepository: AuthorRepository
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initDB() {
        val author = authorRepository.save(Author(null, "Author"))
        documentRepository.save(Document(null, "Title 1", "test.url", listOf(author), "content", "shortDescription", "description"))
        documentRepository.save(Document(null, "Title 2", "test.url", listOf(author), "content", "shortDescription", "description"))
        documentRepository.save(Document(null, "Title 3", "test.url", listOf(author), "content", "shortDescription", "description"))
    }

    fun findAll(): MutableIterable<Document> {
        return documentRepository.findAll()
    }

    fun findById(id: Long): Document? {
        return documentRepository.findByIdOrNull(id)
    }

    fun findAllByTitle(title: String): List<Document> {
        return documentRepository.findAllByTitle(title)
    }

    fun save(document: Document): Document {
        return documentRepository.save(document)
    }

    fun update(document: Document): Document {
        return documentRepository.save(document)
    }
}