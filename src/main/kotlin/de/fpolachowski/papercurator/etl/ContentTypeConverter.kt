package de.fpolachowski.papercurator.etl

import de.fpolachowski.papercurator.model.ContentType

interface ContentTypeConverter {
    fun convert(urlContentType : String) : ContentType
}