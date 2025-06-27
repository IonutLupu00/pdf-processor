package org.jwlf_api.pdf_processor;

import lombok.Getter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestAsserter {

    public static void assertZipContent(StreamingResponseBody result, Map<String, FileExpectation> expectedFiles) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        result.writeTo(byteArrayOutputStream);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {

            Set<String> processedFiles = new HashSet<>();
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                processedFiles.add(fileName);

                byte[] fileBytes = zipInputStream.readAllBytes();

                assertTrue(expectedFiles.containsKey(fileName), "Unexpected file in ZIP: " + fileName);
                FileExpectation expectation = expectedFiles.get(fileName);

                if (expectation.getExpectedSizeBytes() != null) {
                    assertEquals(expectation.getExpectedSizeBytes(), entry.getSize(), "File " + fileName + " size mismatch");
                }

                if (expectation.getCustomAssertion() != null) {
                    expectation.getCustomAssertion().accept(fileName, fileBytes);
                }
            }

            assertEquals(expectedFiles.keySet(), processedFiles, "ZIP should contain exactly the expected files");
            zipInputStream.closeEntry();
        }
    }

    public static void assertZipContentWithPdfs(StreamingResponseBody result, Map<String, PdfAssertDetails> assertDetails) throws IOException {
        Map<String, FileExpectation> expectedFiles = assertDetails.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> FileExpectation.withAssertion(getPdfAssertion(entry.getValue()))
                ));
        assertZipContent(result, expectedFiles);
    }

    public static void assertNumberOfPages(byte[] input, int expectedPageCount) {
        try (PDDocument mergedDoc = Loader.loadPDF(input)) {
            assertEquals(expectedPageCount, mergedDoc.getNumberOfPages());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    public static class FileExpectation {

        private final Long expectedSizeBytes;
        private final BiConsumer<String, byte[]> customAssertion;

        private FileExpectation(Long expectedSizeBytes, BiConsumer<String, byte[]> customAssertion) {
            this.expectedSizeBytes = expectedSizeBytes;
            this.customAssertion = customAssertion;
        }

        public static FileExpectation withSize(long size) {
            return new FileExpectation(size, null);
        }

        public static FileExpectation withAssertion(BiConsumer<String, byte[]> assertion) {
            return new FileExpectation(null, assertion);
        }

        public static FileExpectation withSizeAndAssertion(long size, BiConsumer<String, byte[]> assertion) {
            return new FileExpectation(size, assertion);
        }

    }

    public static BiConsumer<String, byte[]> getPdfAssertion(PdfAssertDetails pdfAssertDetails) {
        return (fileName, documentBytes) -> {
            try (PDDocument doc = Loader.loadPDF(documentBytes)) {
                Integer expectedPages = pdfAssertDetails.pageCount();
                if (expectedPages != null) {
                    assertEquals(expectedPages, doc.getNumberOfPages(), "PDF " + fileName + " should have " + expectedPages + " pages");
                }

                Long expectedSizeLimit = pdfAssertDetails.size();
                if (expectedSizeLimit != null) {
                    long actualSize = documentBytes.length;
                    assertTrue(expectedSizeLimit >= actualSize, "Actual size [%s] is bigger than the expected limit [%s].".formatted(actualSize, expectedSizeLimit));
                }

                String expectedTextContent = pdfAssertDetails.textContent();
                if (expectedTextContent != null) {
                    PDDocument document = Loader.loadPDF(documentBytes);
                    PDFTextStripper textStripper = new PDFTextStripper();
                    String actualTextContent = textStripper.getText(document).trim();
                    assertEquals(expectedTextContent, actualTextContent);
                }
            } catch (IOException e) {
                fail("Failed to load PDF " + fileName + ": " + e.getMessage());
            }
        };
    }

    public static record PdfAssertDetails(Integer pageCount, Long size, String textContent) {
    }
}
