package org.jwlf_api.pdf_processor.split;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.TestDataGenerator;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.jwlf_api.pdf_processor.TestAsserter.PdfAssertDetails;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContentWithPdfs;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFiles;

class PdfSplitServicePagesPerFileTest {

    private PdfSplitServicePagesPerFile pdfSplitService;

    @BeforeEach
    void setUp() {
        pdfSplitService = new PdfSplitServicePagesPerFile();
    }

    @Test
    void testSplit_validPagesPerFile() throws PdfException, IOException {
        int originalPagesPerFile = 4;
        List<MultipartFile> files = TestDataGenerator.generateMockPdfFilesWithPages(1, originalPagesPerFile);
        MultipartFile file = files.getFirst();

        int pagesPerFile = 2;
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, "PAGES_PER_FILE=" + pagesPerFile);
        StreamingResponseBody result = pdfSplitService.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(2, null, null), "part_2.pdf", new PdfAssertDetails(2, null, null));
        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplit_invalidPagesPerFile() {
        List<MultipartFile> files = generateMockPdfFiles(1);
        MultipartFile file = files.get(0);
        int pagesPerFile = -1;
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, "PAGES_PER_FILE=" + pagesPerFile);
        assertThrows(PdfException.class, () -> pdfSplitService.processRequest(request));
    }
}