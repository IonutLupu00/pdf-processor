package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfXmpData;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFilesWithMetadata;
import static org.jwlf_api.pdf_processor.TestUtil.getTextContentFromResources;
import static org.jwlf_api.pdf_processor.TestUtil.runWithTestPDDocument;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.PdfMetadataType.DOCUMENT_INFO;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.PdfMetadataType.XMP_CORE;

class PdfPdfMetadataExtractorTest {

    private static final String EXPECTED_DOCUMENT_INFO_1 = "contentExtraction/metadata/expected/documentInfo1.txt";
    private static final String EXPECTED_XMP_CORE_1 = "contentExtraction/metadata/expected/xmpCore1.txt";

    private final PdfMetadataExtractor pdfMetadataExtractor = new PdfMetadataExtractor();

    @Test
    void testExtractMetadata_documentInfo() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfDocumentInformation result = runWithTestPDDocument(file, document -> (PdfDocumentInformation) pdfMetadataExtractor.extractMetadata(DOCUMENT_INFO, document));
        String expectedString = getTextContentFromResources(EXPECTED_DOCUMENT_INFO_1);
        PdfDocumentInformation expected = PdfDocumentInformation.fromJson(expectedString, PdfDocumentInformation.class);

        expected.setCreationDate(result.getCreationDate());
        expected.setModificationDate(result.getModificationDate());
        expected.setCustomMetadata(result.getCustomMetadata());

        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    void testExtractMetadata_xmpCore() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfXmpData result = runWithTestPDDocument(file, document -> (PdfXmpData) pdfMetadataExtractor.extractMetadata(XMP_CORE, document));
        String expectedString = getTextContentFromResources(EXPECTED_XMP_CORE_1);
        PdfXmpData expected = PdfXmpData.fromJson(expectedString, PdfXmpData.class);

        expected.setCreateDate(result.getCreateDate());
        expected.setMetadataDate(result.getMetadataDate());
        expected.setModifyDate(result.getMetadataDate());

        assertEquals(expected.toJson(), result.toJson());
    }
}
