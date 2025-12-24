package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.EmbeddedFileMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType;
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
                return extractEmbeddedFiles(document);
            }

            default -> {
                return null;
            }
        }
    }

    public EmbeddedFileMetadata extractEmbeddedFiles(PDDocument document) {
        List<EmbeddedFileMetadata.EmbeddedFile> embeddedFiles = new ArrayList<>();

        PDDocumentNameDictionary names = document.getDocumentCatalog().getNames();
        if (names == null) {
            return null;
        }

        PDNameTreeNode<PDComplexFileSpecification> embeddedFilesRaw = names.getEmbeddedFiles();
        if (embeddedFilesRaw == null) {
            return null;
        }

        Map<String, PDComplexFileSpecification> files = null;

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
            embeddedFiles.add(new EmbeddedFileMetadata.EmbeddedFile(spec.getFilename(), embeddedFile.getSubtype(), embeddedFile.getSize()));
        }

        return new EmbeddedFileMetadata(embeddedFiles);
    }

}
