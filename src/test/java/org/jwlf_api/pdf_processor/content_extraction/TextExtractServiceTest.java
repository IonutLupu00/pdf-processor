package org.jwlf_api.pdf_processor.content_extraction;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.TestAsserter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContent;
import static org.jwlf_api.pdf_processor.TestUtil.getPdfFromResources;

class TextExtractServiceTest {

    private final PdfTextExtractService pdfTextExtractService = new PdfTextExtractService();

    @Test
    void testExtractTextFromPdf() throws Exception {
        String fileName = "basic-text-cleaned.pdf";
        MultipartFile pdfWithText = getPdfFromResources(fileName, fileName);
        PdfContentExtractRequest request = new PdfContentExtractRequest(pdfWithText, "", PdfContentExtractType.TEXT);

        StreamingResponseBody result = pdfTextExtractService.processRequest(request);

        String expectedFileName = "basic-text-cleaned.txt";
        String expectedContentResourcesPath = "contentExtraction/text/expected/expectedFullContent.txt";
        InputStream expectedContentInputStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(expectedContentResourcesPath));
        String expectedContent = new String(expectedContentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        TestAsserter.FileExpectation expectation = getExpectation(expectedFileName, expectedContent);

        assertZipContent(result, Map.of(expectedFileName, expectation));
    }

    private static TestAsserter.FileExpectation getExpectation(String expectedFileName, String expectedContent) {
        return TestAsserter.FileExpectation.withAssertion((filename, fileBytes) -> {
            assertEquals(expectedFileName, filename);
            String text = new String(fileBytes, StandardCharsets.UTF_8);
            assertEquals(expectedContent, text);
        });
    }
}
