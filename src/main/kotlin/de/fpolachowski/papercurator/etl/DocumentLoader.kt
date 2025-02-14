package de.fpolachowski.papercurator.etl

import de.fpolachowski.papercurator.model.Document

interface DocumentLoader {
    fun read(url : String) : List<Document>
}