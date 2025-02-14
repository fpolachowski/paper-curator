package de.fpolachowski.papercurator.etl

import de.fpolachowski.papercurator.etl.arxiv.PaperDocumentReader
import org.springframework.ai.document.Document
import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig

class PdfDocumentReader() : PaperDocumentReader {
    private val extractedTextFormatter = ExtractedTextFormatter.builder().build()
    private val pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder().withPageExtractedTextFormatter(extractedTextFormatter).build()

    override fun get(url: String): List<Document> {
        val pdfReader = PagePdfDocumentReader(url, pdfDocumentReaderConfig)
        return pdfReader.read()
    }
}