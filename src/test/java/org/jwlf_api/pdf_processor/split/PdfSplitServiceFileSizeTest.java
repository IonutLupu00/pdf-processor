package org.jwlf_api.pdf_processor.split;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.TestDataGenerator;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.jwlf_api.pdf_processor.TestAsserter.PdfAssertDetails;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContentWithPdfs;

class PdfSplitServiceFileSizeTest {

    private final PdfSplitServiceFileSize pdfSplitService = new PdfSplitServiceFileSize();

    @Test
    void testSplit_validFileSplitting() throws PdfException, IOException {
        int originalPages = 5;
        long fileSizeLimit = 850L;
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithPages(1, originalPages).get(0);
        String options = "%s=%s".formatted(SplitOption.FILE_SIZE_BYTES, fileSizeLimit);
        SplitRequest request = new SplitRequest(file, SplitType.FILE_SIZE, options);
        StreamingResponseBody result = pdfSplitService.processRequest(request);
        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of(
                "part_1.pdf", new PdfAssertDetails(1, fileSizeLimit, null),
                "part_2.pdf", new PdfAssertDetails(1, fileSizeLimit, null),
                "part_3.pdf", new PdfAssertDetails(1, fileSizeLimit, null),
                "part_4.pdf", new PdfAssertDetails(1, fileSizeLimit, null),
                "part_5.pdf", new PdfAssertDetails(1, fileSizeLimit, null)
        );

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplit_invalidPdfFile_throwsPdfException() {
        MultipartFile corruptedFile = TestDataGenerator.generateMockGenericFiles(1).get(0);
        SplitRequest request = new SplitRequest(corruptedFile, SplitType.FILE_SIZE, "FILE_SIZE_BYTES=9000");
        PdfException exception = assertThrows(PdfException.class, () -> pdfSplitService.processRequest(request));
        String expectedErrorMessage = "Invalid file type for [file0.txt]. Only PDF files are allowed.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}