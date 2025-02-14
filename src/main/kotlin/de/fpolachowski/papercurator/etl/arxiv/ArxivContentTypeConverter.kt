package de.fpolachowski.papercurator.etl.arxiv

import de.fpolachowski.papercurator.etl.ContentTypeConverter
import de.fpolachowski.papercurator.model.ContentType

class ArxivContentTypeConverter : ContentTypeConverter
{
    private val map : HashMap<String, ContentType> = hashMapOf("text/html" to ContentType.WEBSITE, "application/pdf" to ContentType.PDF)

    override fun convert(urlContentType : String) : ContentType {
        return if(map.containsKey(urlContentType)) {
            map.getValue(urlContentType)
        } else {
            ContentType.WEBSITE
        }
    }
}