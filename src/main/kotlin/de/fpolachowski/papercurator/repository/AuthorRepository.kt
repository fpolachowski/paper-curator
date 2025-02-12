package de.fpolachowski.papercurator.repository

import de.fpolachowski.papercurator.model.Author
import org.springframework.data.repository.CrudRepository

interface AuthorRepository : CrudRepository<Author, Long>