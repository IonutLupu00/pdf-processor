package org.jwlf_api.pdf_processor.split;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.jwlf_api.pdf_processor.TestDataGenerator;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jwlf_api.pdf_processor.TestAsserter.PdfAssertDetails;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContentWithPdfs;

class PdfSplitServicePageRangeTest {

    private final PdfSplitServicePageRanges pdfSplitServicePageRanges = new PdfSplitServicePageRanges();

    @Test
    void split_shouldSplitPdfByPageRangesSuccessfully() throws Exception {
        List<MultipartFile> files = TestDataGenerator.generateMockPdfFilesWithPages(1, 5);
        MultipartFile inputFile = files.get(0);
        SplitRequest request = new SplitRequest();
        request.setFile(inputFile);
        request.setOptions(SplitOption.PAGE_RANGES + "=1-2,3-4,5");
        request.setSplitType(SplitType.PAGE_RANGES);

        StreamingResponseBody responseBody = pdfSplitServicePageRanges.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(2, null, null), "part_2.pdf", new PdfAssertDetails(2, null, null), "part_3.pdf", new PdfAssertDetails(1, null, null));

        assertZipContentWithPdfs(responseBody, expectedFilesAndPages);
    }

    @ParameterizedTest(name = "{index} => pageRanges=''{0}'', expectedMessageContains=''{1}''")
    @CsvSource({
            "'1-2,a-4', 'Invalid page range format'",
            "'4-2', 'Invalid page range: 4-2'"
    })
    void split_shouldThrowException_forInvalidPageRanges(String pageRanges, String expectedMessage) {
        List<MultipartFile> files = TestDataGenerator.generateMockPdfFilesWithPages(1, 5);
        MultipartFile inputFile = files.get(0);
        SplitRequest request = new SplitRequest();
        request.setFile(inputFile);
        request.setOptions(SplitOption.PAGE_RANGES + "=" + pageRanges);
        request.setSplitType(SplitType.PAGE_RANGES);

        PdfException ex = assertThrows(PdfException.class, () -> pdfSplitServicePageRanges.processRequest(request));
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void split_shouldThrowException_forOverlappingPageRanges() {
        List<MultipartFile> files = TestDataGenerator.generateMockPdfFilesWithPages(1, 5);
        MultipartFile inputFile = files.get(0);
        SplitRequest request = new SplitRequest();
        request.setFile(inputFile);
        request.setOptions(SplitOption.PAGE_RANGES + "=1-3,2-4");
        request.setSplitType(SplitType.PAGE_RANGES);

        PdfException ex = assertThrows(PdfException.class, () -> pdfSplitServicePageRanges.processRequest(request));
        assertTrue(ex.getMessage().contains("Page ranges overlap"));
    }

    @Test
    void split_shouldThrowException_forEmptyPageRanges() {
        List<MultipartFile> files = TestDataGenerator.generateMockPdfFilesWithPages(1, 5);
        MultipartFile inputFile = files.get(0);
        SplitRequest request = new SplitRequest();
        request.setFile(inputFile);
        request.setOptions(SplitOption.PAGE_RANGES + "=");
        request.setSplitType(SplitType.PAGE_RANGES);

        PdfException ex = assertThrows(PdfException.class, () -> pdfSplitServicePageRanges.processRequest(request));
        assertTrue(ex.getMessage().contains("Page ranges option is missing"));
    }

    @Test
    void split_shouldSplitSinglePageRangeCorrectly() throws Exception {
        List<MultipartFile> files = TestDataGenerator.generateMockPdfFilesWithPages(1, 5);
        MultipartFile inputFile = files.get(0);
        SplitRequest request = new SplitRequest();
        request.setFile(inputFile);
        request.setOptions(SplitOption.PAGE_RANGES + "=3-3");
        request.setSplitType(SplitType.PAGE_RANGES);

        StreamingResponseBody responseBody = pdfSplitServicePageRanges.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(1, null, null));

        assertZipContentWithPdfs(responseBody, expectedFilesAndPages);
    }
}