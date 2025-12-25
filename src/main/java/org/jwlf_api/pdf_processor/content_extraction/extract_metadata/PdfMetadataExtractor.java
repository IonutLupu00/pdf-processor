package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.EmbeddedFilesMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfPagesMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfSecurityMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfSignatureMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfXmpMetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PdfMetadataExtractor {

    public PdfMetadata extractMetadata(PdfMetadataType pdfMetadataType, PDDocument document) {
        switch (pdfMetadataType) {
            case DOCUMENT_INFO -> {
                PDDocumentInformation rawDocumentInformation = document.getDocumentInformation();
                return new PdfDocumentInformation(rawDocumentInformation);
            }
            case XMP -> {
                return PdfXmpMetadata.of(document);
            }
            case EMBEDDED_FILES -> {
                return extractEmbeddedFilesMetadata(document);
            }

            case SECURITY -> {
                return extractSecurityMetadata(document);
            }

            case SIGNATURES -> {
                return extractSignatureMetadata(document);
            }

            case PAGES -> {
                return extractPagesMetadata(document);
            }

            default -> throw new IllegalArgumentException("Unsupported pdf metadata type: " + pdfMetadataType);
        }
    }

    public EmbeddedFilesMetadata extractEmbeddedFilesMetadata(PDDocument document) {
        List<EmbeddedFilesMetadata.EmbeddedFileMetadata> embeddedFileMetadataList = new ArrayList<>();

        PDDocumentNameDictionary names = document.getDocumentCatalog().getNames();
        if (names == null) {
            return null;
        }

        PDNameTreeNode<PDComplexFileSpecification> embeddedFilesRaw = names.getEmbeddedFiles();
        if (embeddedFilesRaw == null) {
            return null;
        }

        Map<String, PDComplexFileSpecification> files;

        try {
            files = embeddedFilesRaw.getNames();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get embedded file names.");
        }

        if (files == null) {
            return null;
        }

        for (PDComplexFileSpecification spec : files.values()) {
            PDEmbeddedFile embeddedFile = spec.getEmbeddedFile();
            if (embeddedFile == null) {
                continue;
            }
            embeddedFileMetadataList.add(new EmbeddedFilesMetadata.EmbeddedFileMetadata(spec.getFilename(), embeddedFile.getSubtype(), embeddedFile.getSize()));
        }

        return new EmbeddedFilesMetadata(embeddedFileMetadataList);
    }


    public PdfSecurityMetadata extractSecurityMetadata(PDDocument document) {
        PdfSecurityMetadata metadata = new PdfSecurityMetadata();

        boolean encrypted = document.isEncrypted();
        metadata.setEncrypted(encrypted);

        if (!encrypted) {
            return metadata;
        }

        PDEncryption encryption = document.getEncryption();
        AccessPermission permissions = document.getCurrentAccessPermission();

        metadata.setFilter(encryption.getFilter());

        metadata.setCanPrint(permissions.canPrint());
        metadata.setCanModify(permissions.canModify());
        metadata.setCanModifyAnnotations(permissions.canModifyAnnotations());
        metadata.setCanExtractContent(permissions.canExtractContent());
        metadata.setCanExtractForAccessibility(permissions.canExtractForAccessibility());
        metadata.setCanFillInForm(permissions.canFillInForm());
        metadata.setCanAssembleDocument(permissions.canAssembleDocument());

        return metadata;
    }

    public PdfSignatureMetadata extractSignatureMetadata(PDDocument document) {

        PdfSignatureMetadata metadata = new PdfSignatureMetadata();

        List<PDSignature> signatureList = document.getSignatureDictionaries();
        metadata.setHasSignatures(!signatureList.isEmpty());
        metadata.setSignatureCount(signatureList.size());

        List<PdfSignatureMetadata.PdfSignatureInfo> signatures = new ArrayList<>();

        for (PDSignature sig : signatureList) {
            PdfSignatureMetadata.PdfSignatureInfo info = new PdfSignatureMetadata.PdfSignatureInfo();
            info.setName(sig.getName());
            info.setLocation(sig.getLocation());
            info.setReason(sig.getReason());
            info.setContactInfo(sig.getContactInfo());
            info.setSigningTime(sig.getSignDate().toInstant());
            info.setCoversWholeDocument(sig.getByteRange() != null && sig.getByteRange().length == 4);
            signatures.add(info);
        }

        metadata.setSignatures(signatures);

        return metadata;
    }


    public PdfPagesMetadata extractPagesMetadata(PDDocument document) {
        PdfPagesMetadata metadata = new PdfPagesMetadata();
        List<PdfPagesMetadata.PdfPageMetadata> pages = new ArrayList<>();

        int pageNum = 0;
        for (PDPage page : document.getPages()) {
            pageNum++;
            PDRectangle mediaBox = page.getMediaBox();
            PdfPagesMetadata.PdfPageMetadata pageMetadata = new PdfPagesMetadata.PdfPageMetadata();
            pageMetadata.setPageNumber(pageNum);
            pageMetadata.setWidth(mediaBox.getWidth());
            pageMetadata.setHeight(mediaBox.getHeight());
            pageMetadata.setRotation(page.getRotation());
            pages.add(pageMetadata);
        }

        metadata.setNumberOfPages(pages.size());
        metadata.setPages(pages);
        return metadata;
    }

}
