package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jwlf_api.pdf_processor.TestUtil.BASIC_TEXT_PDF;
import static org.jwlf_api.pdf_processor.TestUtil.getTextContentFromResources;
import static org.jwlf_api.pdf_processor.TestUtil.runWithTestPDDocument;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.PdfMetadataType.DOCUMENT_INFO;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.PdfMetadataType.XMP_CORE;

public class  PdfPdfMetadataExtractorTest {

    private static final String EXPECTED_DOCUMENT_INFO_1 = "contentExtraction/metadata/expected/documentInfo1.txt";
    private static final String EXPECTED_XMP_CORE_1 = "contentExtraction/metadata/expected/xmpCore.txt";

    private final PdfMetadataExtractor pdfMetadataExtractor = new PdfMetadataExtractor();

    //TODO: use pdf file that covers all the data. Dynamically generate or find existing one.

    @Test
    void testExtractMetadata_documentInfo() {
        PdfMetadata result = runWithTestPDDocument(BASIC_TEXT_PDF,
                document -> pdfMetadataExtractor.extractMetadata(DOCUMENT_INFO, document));
        String expectedString = getTextContentFromResources(EXPECTED_DOCUMENT_INFO_1);
        PdfDocumentInformation expected = PdfDocumentInformation.fromJson(expectedString, PdfDocumentInformation.class);
        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    void testExtractMetadata_xmpCore() {
        PdfMetadata result = runWithTestPDDocument(BASIC_TEXT_PDF,
                document -> pdfMetadataExtractor.extractMetadata(XMP_CORE, document));
        String expectedString = getTextContentFromResources(EXPECTED_XMP_CORE_1);
        PdfDocumentInformation expected = PdfDocumentInformation.fromJson(expectedString, PdfDocumentInformation.class);
        assertEquals(expected.toJson(), result.toJson());
    }
}
