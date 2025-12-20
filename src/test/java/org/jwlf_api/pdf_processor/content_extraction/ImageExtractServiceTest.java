package org.jwlf_api.pdf_processor.content_extraction;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jwlf_api.pdf_processor.TestAsserter.FileExpectation;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContent;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFilesWithImages;

class ImageExtractServiceTest {

    private final PdfContentExtractService imageExtractService = new PdfImageExtractService();

    @Test
    void testExtractImage_success() throws IOException, PdfContentExtractException {
        MultipartFile pdfWithImages = generateMockPdfFilesWithImages(1, 1).getFirst();
        PdfContentExtractRequest request = new PdfContentExtractRequest(pdfWithImages, PdfContentExtractType.IMAGE);
        StreamingResponseBody result = (StreamingResponseBody) imageExtractService.processRequest(request);
        String expectedFileName = "page_1_image_1.png";
        FileExpectation expectation = getExpectation(expectedFileName, 100, 100);
        Map<String, FileExpectation> expectedFiles = Map.of(expectedFileName, expectation);
        assertZipContent(result, expectedFiles);
    }

    private static FileExpectation getExpectation(String expectedFileName, int expectedWidth, int expectedHeight) {
        return FileExpectation.withAssertion((filename, fileBytes) -> {
            assertEquals(expectedFileName, filename);
            assertTrue(fileBytes.length > 0);
            assertFileIsPng(fileBytes);
            try {
                assertImageDimensions(fileBytes, expectedWidth, expectedHeight);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void assertFileIsPng(byte[] fileBytes) {
        assertTrue(fileBytes[0] == (byte) 0x89 && fileBytes[1] == 'P' && fileBytes[2] == 'N' && fileBytes[3] == 'G');
    }

    private static void assertImageDimensions(byte[] fileBytes, int expectedWidth, int expectedHeight) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
        assertEquals(expectedWidth, image.getWidth());
        assertEquals(expectedHeight, image.getHeight());
    }
}
