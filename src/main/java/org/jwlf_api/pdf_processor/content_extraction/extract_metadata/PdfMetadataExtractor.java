package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfXmpMetadata;
import org.springframework.stereotype.Component;

@Component
public class PdfMetadataExtractor {

    public PdfMetadata extractMetadata(PdfMetadataType pdfMetadataType, PDDocument document) {
        switch (pdfMetadataType){
            case DOCUMENT_INFO -> {
                PDDocumentInformation rawDocumentInformation = document.getDocumentInformation();
                return new PdfDocumentInformation(rawDocumentInformation);
            }
            case XMP_CORE -> {
                return PdfXmpMetadata.of(document);
            }
            //TODO: add missing
            default -> {
                return null;
            }
        }
    }

}
