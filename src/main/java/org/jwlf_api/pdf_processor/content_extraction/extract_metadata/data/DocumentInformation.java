package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class DocumentInformation implements PdfMetadata {

    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;
    private Instant creationDate;
    private Instant modificationDate;
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
        this.creationDate = pdDocumentInformation.getCreationDate().toInstant();
        this.modificationDate = pdDocumentInformation.getModificationDate().toInstant();
        this.trapped = pdDocumentInformation.getTrapped();

        pdDocumentInformation.getCOSObject().entrySet().forEach(entry -> {
            String key = entry.getKey().getName();
            if (!this.custom.containsKey(key)) {
                this.custom.put(key, entry.getValue().toString());
            }
        });
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        put(root, "title", title);
        put(root, "author", author);
        put(root, "subject", subject);
        put(root, "keywords", keywords);
        put(root, "creator", creator);
        put(root, "producer", producer);
        put(root, "creationDate", instantToString(creationDate));
        put(root, "modificationDate", instantToString(modificationDate));
        put(root, "trapped", trapped);

        ObjectNode customNode = mapper.createObjectNode();
        custom.forEach(customNode::put);
        root.set("custom", customNode);

        try {
            return mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void put(ObjectNode node, String key, String value) {
        if (value != null) {
            node.put(key, value);
        }
    }

    private static String instantToString(Instant instant) {
        return instant == null ? null : instant.toString();
    }

    public static DocumentInformation fromString(String json) {
        DocumentInformation info = new DocumentInformation(null);
        if (json == null || json.isBlank()) {
            return info;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);

            info.title = text(root, "title");
            info.author = text(root, "author");
            info.subject = text(root, "subject");
            info.keywords = text(root, "keywords");
            info.creator = text(root, "creator");
            info.producer = text(root, "producer");
            info.creationDate = stringToInstant(text(root, "creationDate"));
            info.modificationDate = stringToInstant(text(root, "modificationDate"));
            info.trapped = text(root, "trapped");

            JsonNode customNode = root.get("custom");
            if (customNode != null && customNode.isObject()) {
                customNode.fields().forEachRemaining(e ->
                        info.custom.put(e.getKey(), e.getValue().asText())
                );
            }

            return info;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? null : v.asText();
    }

    private static Instant stringToInstant(String value) {
        if (value == null) {
            return null;
        }
        return Instant.parse(value);
    }

}