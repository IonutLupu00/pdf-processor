package org.jwlf_api.pdf_processor.split;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.HashMap;
import java.util.Map;

import static org.jwlf_api.pdf_processor.TestAsserter.PdfAssertDetails;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContentWithPdfs;
import static org.jwlf_api.pdf_processor.TestUtil.getPdfFromResources;

class PdfSplitServiceCodeTest {

    private PdfSplitServiceCode pdfSplitService;

    @BeforeEach
    void setUp() {
        pdfSplitService = new PdfSplitServiceCode();
    }

    @Test
    void testSplitPdfByCode_codeEachPage() throws Exception {
        String resourcesFileName = "barcode_each_page_12_pages.pdf";
        MultipartFile file = getPdfFromResources(resourcesFileName, resourcesFileName);
        SplitRequest request = SplitRequest.builder()
                .file(file)
                .splitType(SplitType.CODE).build();

        StreamingResponseBody result = pdfSplitService.processRequest(request);

        int expectedResultFilesCount = 5;
        Map<String, PdfAssertDetails> assertDetailsMap = new HashMap<>(expectedResultFilesCount);
        for (int i = 1; i <= expectedResultFilesCount; i++) {
            String expectedFileName = "part_%s.pdf".formatted(i);
            PdfAssertDetails assertDetails = new PdfAssertDetails(1, null, null);
            assertDetailsMap.put(expectedFileName, assertDetails);
        }

        assertZipContentWithPdfs(result, assertDetailsMap);
    }

}
