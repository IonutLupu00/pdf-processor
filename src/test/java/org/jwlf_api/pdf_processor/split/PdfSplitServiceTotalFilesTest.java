package org.jwlf_api.pdf_processor.split;

import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.jwlf_api.pdf_processor.TestAsserter.PdfAssertDetails;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContentWithPdfs;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockGenericFiles;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFilesWithPages;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFilesWithTotalSize;
import static org.jwlf_api.pdf_processor.TestUtil.getPdfFromResources;
import static org.jwlf_api.pdf_processor.TestUtil.getTextContentFromResources;

class PdfSplitServiceTotalFilesTest {

    private final PdfSplitServiceTotalFiles pdfSplitService = new PdfSplitServiceTotalFiles();

    @Test
    void testSplitTotalFiles_success() throws PdfException, IOException {
        List<MultipartFile> files = generateMockPdfFilesWithPages(1, 2);
        MultipartFile file = files.get(0);
        int totalFiles = 2;
        String options = SplitOption.TOTAL_FILES + "=" + totalFiles;
        SplitRequest request = new SplitRequest(file, SplitType.TOTAL_FILES, options);
        StreamingResponseBody result = pdfSplitService.processRequest(request);
        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(1, null, null), "part_2.pdf", new PdfAssertDetails(1, null, null));

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitTotalFiles_generatedInput_multiplePages() throws Exception {
        String resourcesFileName = "sample-multipage-binary-text.pdf";
        MultipartFile file = getPdfFromResources(resourcesFileName, resourcesFileName);
        int totalFiles = 2;
        String options = SplitOption.TOTAL_FILES + "=" + totalFiles;
        SplitRequest request = new SplitRequest(file, SplitType.TOTAL_FILES, options);
        StreamingResponseBody result = pdfSplitService.processRequest(request);

        String expectedFile1Path = "split/totalFiles/expectedContentMultipage_file1.txt";
        String expectedContentFile1 = getTextContentFromResources(expectedFile1Path);

        String expectedFile2Path = "split/totalFiles/expectedContentMultipage_file2.txt";
        String expectedContentFile2 = getTextContentFromResources(expectedFile2Path);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of(
                "part_1.pdf", new PdfAssertDetails(5, null, expectedContentFile1),
                "part_2.pdf", new PdfAssertDetails(5, null, expectedContentFile2)
        );

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitTotalFiles_invalidOption_throwsException() {
        MultipartFile file = generateMockPdfFilesWithPages(1, 2).get(0);
        String options = "TOTAL_FILES=0";

        SplitRequest request = new SplitRequest(file, SplitType.TOTAL_FILES, options);

        assertThrows(PdfException.class, () -> pdfSplitService.processRequest(request));
    }

    @Test
    void testSplitTotalFiles_fileTooLarge_throwsException() {
        MultipartFile file = generateMockPdfFilesWithTotalSize(1, 2 * 1024 * 1024).get(0);
        String options = SplitOption.TOTAL_FILES + "=1";

        SplitRequest request = new SplitRequest(file, SplitType.TOTAL_FILES, options);

        assertThrows(PdfException.class, () -> pdfSplitService.processRequest(request));
    }

    @Test
    void testSplitTotalFiles_multipleFiles() throws PdfException, IOException {
        List<MultipartFile> files = generateMockPdfFilesWithPages(2, 3);
        for (MultipartFile file : files) {
            int totalFiles = 3;
            String options = SplitOption.TOTAL_FILES + "=" + totalFiles;
            SplitRequest request = new SplitRequest(file, SplitType.TOTAL_FILES, options);

            StreamingResponseBody result = pdfSplitService.processRequest(request);

            Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of(
                    "part_1.pdf", new PdfAssertDetails(1, null, null),
                    "part_2.pdf", new PdfAssertDetails(1, null, null),
                    "part_3.pdf", new PdfAssertDetails(1, null, null)
            );
            assertZipContentWithPdfs(result, expectedFilesAndPages);
        }
    }

    @Test
    void testSplitTotalFiles_nonPdfFile_throwsException() {
        MultipartFile file = generateMockGenericFiles(1).get(0);
        String options = SplitOption.TOTAL_FILES + "=1";
        SplitRequest request = new SplitRequest(file, SplitType.TOTAL_FILES, options);

        assertThrows(PdfException.class, () -> pdfSplitService.processRequest(request));
    }
}
