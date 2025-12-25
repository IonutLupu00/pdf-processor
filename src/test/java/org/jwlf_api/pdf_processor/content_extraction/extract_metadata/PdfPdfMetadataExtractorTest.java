package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfEmbeddedFilesMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfPagesMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfSecurityMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfSignatureMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfXmpMetadata;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFilesWithMetadata;
import static org.jwlf_api.pdf_processor.TestUtil.getTextContentFromResources;
import static org.jwlf_api.pdf_processor.TestUtil.runWithTestPDDocument;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.DOCUMENT_INFO;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.EMBEDDED_FILES;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.PAGES;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.SECURITY;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.SIGNATURES;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.XMP;

class PdfPdfMetadataExtractorTest {

    private static final String EXPECTED_DOCUMENT_INFO_1 = "contentExtraction/metadata/expected/documentInfo1.txt";
    private static final String EXPECTED_XMP_CORE_1 = "contentExtraction/metadata/expected/xmp1.txt";
    private static final String EXPECTED_EMBEDDED_FILES_1 = "contentExtraction/metadata/expected/embeddedFiles1.txt";
    private static final String EXPECTED_SECURITY_1 = "contentExtraction/metadata/expected/security1.txt";
    private static final String EXPECTED_SIGNATURES_1 = "contentExtraction/metadata/expected/signatures1.txt";
    private static final String EXPECTED_PAGES_1 = "contentExtraction/metadata/expected/pages1.txt";

    private final PdfMetadataExtractor pdfMetadataExtractor = new PdfMetadataExtractor();

    @Test
    void testExtractMetadata_documentInfo() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfDocumentInformation result = runWithTestPDDocument(file, document -> (PdfDocumentInformation) pdfMetadataExtractor.extractMetadata(DOCUMENT_INFO, document));
        String expectedString = getTextContentFromResources(EXPECTED_DOCUMENT_INFO_1);
        PdfDocumentInformation expected = PdfDocumentInformation.fromJson(expectedString, PdfDocumentInformation.class);

        expected.setCreationDate(result.getCreationDate());
        expected.setModificationDate(result.getModificationDate());

        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    void testExtractMetadata_xmp() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfXmpMetadata result = runWithTestPDDocument(file, document -> (PdfXmpMetadata) pdfMetadataExtractor.extractMetadata(XMP, document));
        String expectedString = getTextContentFromResources(EXPECTED_XMP_CORE_1);
        PdfXmpMetadata expected = PdfXmpMetadata.fromJson(expectedString, PdfXmpMetadata.class);

        expected.xmpBasic.setMetadataDate(result.getXmpBasic().getMetadataDate());
        expected.xmpBasic.setMetadataDate(result.getXmpBasic().getMetadataDate());
        expected.xmpBasic.setModifyDate(result.getXmpBasic().getModifyDate());
        expected.xmpBasic.setCreateDate(result.getXmpBasic().getCreateDate());

        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    void testExtractMetadata_embeddedFiles() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfEmbeddedFilesMetadata result = runWithTestPDDocument(file, document ->
                (PdfEmbeddedFilesMetadata) pdfMetadataExtractor.extractMetadata(EMBEDDED_FILES, document));
        String expectedString = getTextContentFromResources(EXPECTED_EMBEDDED_FILES_1);
        PdfXmpMetadata expected = PdfXmpMetadata.fromJson(expectedString, PdfXmpMetadata.class);

        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    void testExtractMetadata_security() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfSecurityMetadata result = runWithTestPDDocument(file, document ->
                (PdfSecurityMetadata) pdfMetadataExtractor.extractMetadata(SECURITY, document));
        String expectedString = getTextContentFromResources(EXPECTED_SECURITY_1);
        PdfXmpMetadata expected = PdfXmpMetadata.fromJson(expectedString, PdfXmpMetadata.class);

        assertEquals(expected.toJson(), result.toJson());
    }
    @Test
    void testExtractMetadata_signatures() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfSignatureMetadata result = runWithTestPDDocument(file, document ->
                (PdfSignatureMetadata) pdfMetadataExtractor.extractMetadata(SIGNATURES, document));
        String expectedString = getTextContentFromResources(EXPECTED_SIGNATURES_1);
        PdfXmpMetadata expected = PdfXmpMetadata.fromJson(expectedString, PdfXmpMetadata.class);

        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    void testExtractMetadata_pages() {
        MultipartFile file = generateMockPdfFilesWithMetadata(1).getFirst();
        PdfPagesMetadata result = runWithTestPDDocument(file, document ->
                (PdfPagesMetadata) pdfMetadataExtractor.extractMetadata(PAGES, document));
        String expectedString = getTextContentFromResources(EXPECTED_PAGES_1);
        PdfXmpMetadata expected = PdfXmpMetadata.fromJson(expectedString, PdfXmpMetadata.class);

        assertEquals(expected.toJson(), result.toJson());
    }

}
