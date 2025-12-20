package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.DocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.Metadata;

public class MetadataExtractor {

    private MetadataExtractor() {
    }

    public Metadata extractMetadata(MetadataType metadataType, PDDocument document) {
        switch (metadataType){
            case DOCUMENT_INFO -> {
                return extractDocumentInformation(document);
            }
            default -> throw new IllegalArgumentException("Invalid metadata.");
        }
    }

    private static DocumentInformation extractDocumentInformation(PDDocument document) {
        PDDocumentInformation rawDocumentInformation = document.getDocumentInformation();
        return new DocumentInformation(rawDocumentInformation);
    }

}
