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
public class PdfXmpCore extends PdfMetadata {

    private String creatorTool;
    private Instant createDate;
    private Instant modifyDate;
    private Instant metadataDate;
    private List<String> identifiers;
    private List<String> advisory;
    private String label;
    private Integer rating;
    private String nickname;
    private String baseURL;
    public PdfXmpCore() {
        //empty constructor
    }

    public static PdfXmpCore of(PDDocument document) {
        try {
            if(document == null) {
                return new PdfXmpCore();
            }
            
            PDMetadata metadata = document.getDocumentCatalog().getMetadata();
            if (metadata == null) {
                return new PdfXmpCore();
            }
            InputStream is = metadata.createInputStream();

            DomXmpParser parser = new DomXmpParser();
            XMPMetadata xmp = parser.parse(is);

            XMPBasicSchema xmpBasicSchema = xmp.getXMPBasicSchema();
            if (xmpBasicSchema == null) {
                return new PdfXmpCore();
            }
            PdfXmpCore resultData = new PdfXmpCore();
            resultData.setCreatorTool(xmpBasicSchema.getCreatorTool());
            if (xmpBasicSchema.getCreateDate() != null) {
                resultData.setCreateDate(xmpBasicSchema.getCreateDate().toInstant());
            }
            if (xmpBasicSchema.getModifyDate() != null) {
                resultData.setModifyDate(xmpBasicSchema.getModifyDate().toInstant());
            }
            if (xmpBasicSchema.getMetadataDate() != null) {
                resultData.setMetadataDate(xmpBasicSchema.getMetadataDate().toInstant());
            }
            resultData.setIdentifiers(xmpBasicSchema.getIdentifiers());
            resultData.setLabel(xmpBasicSchema.getLabel());
            resultData.setRating(xmpBasicSchema.getRating());
            resultData.setNickname(xmpBasicSchema.getNickname());
            resultData.setBaseURL(xmpBasicSchema.getBaseURL());
            resultData.setAdvisory(xmpBasicSchema.getAdvisory());
            
            return resultData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read XMP Core metadata", e);
        }
    }
}