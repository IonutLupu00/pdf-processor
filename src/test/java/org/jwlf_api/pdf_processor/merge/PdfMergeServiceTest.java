package org.jwlf_api.pdf_processor.merge;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jwlf_api.pdf_processor.TestAsserter.assertNumberOfPages;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockGenericFiles;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFiles;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PdfMergeServiceTest {

    private final PdfMergeService pdfMergeService = new PdfMergeService();
    

    @Test
    void testMerge_returnsStreamingResponseBody() throws PdfMergeException, IOException, InterruptedException {
        List<MultipartFile> files = generateMockPdfFiles(3);
        MergeRequest mergeRequest = new MergeRequest();
        mergeRequest.setFiles(files);
        StreamingResponseBody responseBody = pdfMergeService.processRequest(mergeRequest);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseBody.writeTo(outputStream);
        assertTrue(outputStream.size() > 0);
        assertNumberOfPages(outputStream.toByteArray(), 3);
    }

    @Test
    void testMerge_throwsPdfMergeExceptionOnIOException() {
        MultipartFile badFile = mock(MultipartFile.class);
        MergeRequest mergeRequest = new MergeRequest();
        mergeRequest.setFiles(List.of(badFile));
        when(badFile.getContentType()).thenReturn("incorrect_type");
        PdfMergeException exception = assertThrows(PdfMergeException.class, () -> pdfMergeService.processRequest(mergeRequest));
        String message = "Invalid file type for [null]. Only PDF files are allowed.";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMerge_wrongFileType() {
        List<MultipartFile> files = generateMockGenericFiles(2);
        MergeRequest mergeRequest = new MergeRequest();
        mergeRequest.setFiles(files);
        String expectedErrorMessage = "Invalid file type for [file0.txt]. Only PDF files are allowed.";
        PdfMergeException exception = assertThrows(PdfMergeException.class, () -> pdfMergeService.processRequest(mergeRequest));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
