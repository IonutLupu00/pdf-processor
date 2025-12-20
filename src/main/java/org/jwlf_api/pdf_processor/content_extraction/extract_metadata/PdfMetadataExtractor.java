package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.DocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;

public class PdfMetadataExtractor {

    private PdfMetadataExtractor() {
    }

    public static PdfMetadata extractMetadata(PdfMetadataType pdfMetadataType, PDDocument document) {
        switch (pdfMetadataType){
            case DOCUMENT_INFO -> {
                return extractDocumentInformation(document);
            }
            //TODO: add missing
            default -> throw new IllegalArgumentException("Invalid metadata.");
        }
    }

    private static DocumentInformation extractDocumentInformation(PDDocument document) {
        PDDocumentInformation rawDocumentInformation = document.getDocumentInformation();
        return new DocumentInformation(rawDocumentInformation);
    }

}
