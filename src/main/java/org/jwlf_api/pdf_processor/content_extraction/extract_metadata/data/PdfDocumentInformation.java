package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;


import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.time.Instant;

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
    private String trapped;

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
        this.creationDate = pdDocumentInformation.getCreationDate().toInstant();
        this.modificationDate = pdDocumentInformation.getModificationDate().toInstant();
        this.trapped = pdDocumentInformation.getTrapped();
    }
}