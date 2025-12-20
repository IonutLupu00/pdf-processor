package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.DocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jwlf_api.pdf_processor.TestUtil.BASIC_TEXT_PDF;
import static org.jwlf_api.pdf_processor.TestUtil.getTextContentFromResources;
import static org.jwlf_api.pdf_processor.TestUtil.runWithTestPDDocument;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.PdfMetadataType.DOCUMENT_INFO;

public class  PdfPdfMetadataExtractorTest {

    private static final String EXPECTED_DOCUMENT_INFO_1 = "contentExtraction/metadata/expected/documentInfo1.txt";

    @Test
    void testExtractMetadata_documentInfo() {
        PdfMetadata result = runWithTestPDDocument(BASIC_TEXT_PDF,
                document -> PdfMetadataExtractor.extractMetadata(DOCUMENT_INFO, document));
        String expectedString = getTextContentFromResources(EXPECTED_DOCUMENT_INFO_1);
        DocumentInformation expected = DocumentInformation.fromString(expectedString);
        assertEquals(expected, result);
    }
}
