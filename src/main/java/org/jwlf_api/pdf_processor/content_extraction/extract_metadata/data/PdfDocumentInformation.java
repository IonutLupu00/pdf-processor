package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;


import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class PdfDocumentInformation extends PdfMetadata {
    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;
    private Instant creationDate;
    private Instant modificationDate;
    private Trapped trapped;
    private Map<String, String> customMetadata;
    public PdfDocumentInformation() {
    }

    public PdfDocumentInformation(PDDocumentInformation pdDocumentInformation) {
        if (pdDocumentInformation == null) {
            return;
        }

        this.title = pdDocumentInformation.getTitle();
        this.author = pdDocumentInformation.getAuthor();
        this.subject = pdDocumentInformation.getSubject();
        this.keywords = pdDocumentInformation.getKeywords();
        this.creator = pdDocumentInformation.getCreator();
        this.producer = pdDocumentInformation.getProducer();
        this.trapped = Trapped.fromValue(pdDocumentInformation.getTrapped());
        
        if (pdDocumentInformation.getCreationDate() != null) {
            this.creationDate = pdDocumentInformation.getCreationDate().toInstant();
        }

        if (pdDocumentInformation.getModificationDate() != null) {
            this.modificationDate = pdDocumentInformation.getModificationDate().toInstant();
        }

        Set<String> coveredKeys = Set.of("CreationDate", "Keywords", "Creator", "Producer", "Trapped", "Title", "ModDate", "Subject", "Author");
        
        this.customMetadata = new HashMap<>();
        for (String key : pdDocumentInformation.getMetadataKeys()) {
            String value = pdDocumentInformation.getCustomMetadataValue(key);
            if (value != null && !coveredKeys.contains(key)) {
                this.customMetadata.put(key, value);
            }
        }
    }
}