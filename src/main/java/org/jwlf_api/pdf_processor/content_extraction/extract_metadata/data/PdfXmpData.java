package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;


import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.xml.DomXmpParser;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PdfXmpData extends PdfMetadata {

    private String creatorTool;
    private Instant createDate;
    private Instant modifyDate;
    private Instant metadataDate;
    private List<String> identifiers;
    private String label;
    private Integer rating;

    public PdfXmpData() {
    }

    public static PdfXmpData of(PDDocument document) {
        try {
            PDMetadata metadata = document.getDocumentCatalog().getMetadata();
            if (metadata == null) {
                return null;
            }
            InputStream is = metadata.createInputStream();

            DomXmpParser parser = new DomXmpParser();
            XMPMetadata xmp = parser.parse(is);

            XMPBasicSchema schema = xmp.getXMPBasicSchema();
            if (schema == null) {
                return null;
            }
            PdfXmpData data = new PdfXmpData();
            data.setCreatorTool(schema.getCreatorTool());
            if (schema.getCreateDate() != null) {
                data.setCreateDate(schema.getCreateDate().toInstant());
            }
            if (schema.getModifyDate() != null) {
                data.setModifyDate(schema.getModifyDate().toInstant());
            }
            if (schema.getMetadataDate() != null) {
                data.setMetadataDate(schema.getMetadataDate().toInstant());
            }
            data.setIdentifiers(schema.getIdentifiers());
            data.setLabel(schema.getLabel());
            data.setRating(schema.getRating());

            return data;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read XMP Core metadata", e);
        }
    }
}