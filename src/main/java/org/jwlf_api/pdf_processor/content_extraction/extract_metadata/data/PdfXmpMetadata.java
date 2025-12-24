package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.schema.XMPRightsManagementSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.DomXmpParser;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PdfXmpMetadata extends PdfMetadata {

    public DublinCore dublinCore;
    public XmpBasic xmpBasic;
    public PdfSchema pdfSchema;
    public PdfA pdfA;
    public PdfX pdfB;
    public XmpRights xmpRights;

    public static PdfXmpMetadata of(PDDocument document) {
        try {
            if (document == null) {
                return new PdfXmpMetadata();
            }

            PDMetadata metadata = document.getDocumentCatalog().getMetadata();
            if (metadata == null) {
                return new PdfXmpMetadata();
            }
            InputStream is = metadata.createInputStream();

            DomXmpParser parser = new DomXmpParser();
            XMPMetadata xmp = parser.parse(is);
            PdfXmpMetadata resultData = new PdfXmpMetadata();

            resultData = enrichWithXmpBasicMetadata(xmp, resultData);
            resultData = enrichWithDublinCoreMetadata(xmp, resultData);
            resultData = enrichWithPdfASchema(xmp, resultData);
            resultData = enrichWithPdfSchema(xmp, resultData);
            resultData = enrichWithXmpRights(xmp, resultData);

            return resultData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read XMP Core metadata", e);
        }
    }

    private static PdfXmpMetadata enrichWithXmpBasicMetadata(XMPMetadata xmp, PdfXmpMetadata resultData) {
        if (xmp == null || resultData == null) {
            return resultData;
        }

        XMPBasicSchema basicSchema = xmp.getXMPBasicSchema();
        if (basicSchema != null) {
            XmpBasic xmpBasic = new XmpBasic();
            xmpBasic.setCreateDate(basicSchema.getCreateDate().toInstant());
            xmpBasic.setModifyDate(basicSchema.getModifyDate().toInstant());
            xmpBasic.setMetadataDate(basicSchema.getMetadataDate().toInstant());
            xmpBasic.setCreatorTool(basicSchema.getCreatorTool());
            xmpBasic.setLabel(basicSchema.getLabel());
            xmpBasic.setRating(basicSchema.getRating());
            xmpBasic.setIdentifiers(basicSchema.getIdentifiers());
            resultData.setXmpBasic(xmpBasic);
        }

        return resultData;
    }


    private static PdfXmpMetadata enrichWithDublinCoreMetadata(XMPMetadata xmp, PdfXmpMetadata resultData) {
        if (xmp == null || resultData == null) {
            return resultData;
        }

        DublinCoreSchema dcSchema = xmp.getDublinCoreSchema();
        if (dcSchema == null) {
            return resultData;
        }

        try {
            DublinCore dublinCore = new DublinCore();
            dublinCore.setTitle(dcSchema.getTitle());
            dublinCore.setCreators(dcSchema.getCreators());
            dublinCore.setSubjects(dcSchema.getSubjects());
            dublinCore.setDescription(dcSchema.getDescription());
            dublinCore.setPublishers(dcSchema.getPublishers());
            dublinCore.setContributors(dcSchema.getContributors());
            dublinCore.setTypes(dcSchema.getTypes());
            dublinCore.setFormat(dcSchema.getFormat());
            dublinCore.setIdentifier(dcSchema.getIdentifier());
            dublinCore.setSource(dcSchema.getSource());
            dublinCore.setLanguages(dcSchema.getLanguages());
            dublinCore.setRelations(dcSchema.getRelations());
            dublinCore.setCoverage(dcSchema.getCoverage());
            dublinCore.setRights(dcSchema.getRights());

            resultData.setDublinCore(dublinCore);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Dublin Core metadata", e);
        }

        return resultData;
    }

    private static PdfXmpMetadata enrichWithPdfSchema(XMPMetadata xmp, PdfXmpMetadata resultData) {
        if (xmp == null || resultData == null) return resultData;

        AdobePDFSchema pdfSchemaXmp = xmp.getAdobePDFSchema();
        if (pdfSchemaXmp != null) {
            PdfSchema pdfSchema = new PdfSchema();
            pdfSchema.setProducer(pdfSchemaXmp.getProducer());
            pdfSchema.setKeywords(pdfSchemaXmp.getKeywords());
            pdfSchema.setPdfVersion(pdfSchemaXmp.getPDFVersion());

            resultData.setPdfSchema(pdfSchema);
        }

        return resultData;
    }

    private static PdfXmpMetadata enrichWithPdfASchema(XMPMetadata xmp, PdfXmpMetadata resultData) {
        if (xmp == null || resultData == null) return resultData;

        PDFAIdentificationSchema pdfAXmp = xmp.getPDFAIdentificationSchema();
        if (pdfAXmp == null) {
            return resultData;
        }
        PdfA pdfa = new PdfA();
        pdfa.setPart(pdfAXmp.getPart());
        pdfa.setConformance(pdfAXmp.getConformance());

        resultData.setPdfA(pdfa);

        return resultData;
    }

    private static PdfXmpMetadata enrichWithXmpRights(XMPMetadata xmp, PdfXmpMetadata resultData) {
        if (xmp == null || resultData == null) return resultData;

        XMPRightsManagementSchema rightsSchema = xmp.getXMPRightsManagementSchema();
        if (rightsSchema == null) {
            return resultData;
        }
        try {
            XmpRights rights = new XmpRights();
            rights.setMarked(rightsSchema.getMarked());
            rights.setOwners(rightsSchema.getOwners());
            rights.setUsageTerms(rightsSchema.getUsageTerms());
            rights.setWebStatement(rightsSchema.getWebStatement());
            resultData.setXmpRights(rights);
        } catch (BadFieldValueException e) {
            throw new RuntimeException("Failed to read XMP rights metadata", e);
        }

        return resultData;
    }


    @Getter
    @Setter
    public static class DublinCore {
        private String title;
        private List<String> creators;
        private List<String> subjects;
        private String description;
        private List<String> publishers;
        private List<String> contributors;
        private Instant date;
        private List<String> types;
        private String format;
        private String identifier;
        private String source;
        private List<String> languages;
        private List<String> relations;
        private String coverage;
        private String rights;
    }

    @Setter
    @Getter
    public static class XmpBasic {
        private Instant createDate;
        private Instant modifyDate;
        private Instant metadataDate;
        private String creatorTool;
        private String label;
        private Integer rating;
        private List<String> identifiers;
    }

    @Getter
    @Setter
    public static class PdfSchema {
        private String producer;
        private String pdfVersion;
        private String keywords;
    }

    @Getter
    @Setter
    public static class PdfA {
        private Integer part;
        private String conformance;
    }

    @Getter
    @Setter
    public static class PdfX {
        private String version;
        private String conformance;
        private List<String> outputIntents;
        private List<String> fonts;
    }

    @Getter
    @Setter
    public static class XmpRights {
        private Boolean marked;
        private List<String> owners;
        private String usageTerms;
        private String webStatement;
    }

}