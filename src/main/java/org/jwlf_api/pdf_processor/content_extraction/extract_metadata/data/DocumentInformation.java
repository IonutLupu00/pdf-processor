package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;


import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Data
public class DocumentInformation implements Metadata {

    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;
    private Calendar creationDate;
    private Calendar modificationDate;
    private String trapped;
    private Map<String, String> custom = new HashMap<>();

    public DocumentInformation(PDDocumentInformation pdDocumentInformation) {
        if (pdDocumentInformation == null) {
            return;
        }

        this.title = pdDocumentInformation.getTitle();
        this.author = pdDocumentInformation.getAuthor();
        this.subject = pdDocumentInformation.getSubject();
        this.keywords = pdDocumentInformation.getKeywords();
        this.creator = pdDocumentInformation.getCreator();
        this.producer = pdDocumentInformation.getProducer();
        this.creationDate = pdDocumentInformation.getCreationDate();
        this.modificationDate = pdDocumentInformation.getModificationDate();
        this.trapped = pdDocumentInformation.getTrapped();

        pdDocumentInformation.getCOSObject().entrySet().forEach(entry -> {
            String key = entry.getKey().getName();
            if (!this.custom.containsKey(key)) {
                this.custom.put(key, entry.getValue().toString());
            }
        });
    }
}