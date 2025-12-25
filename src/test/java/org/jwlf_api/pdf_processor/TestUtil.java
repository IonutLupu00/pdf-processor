package org.jwlf_api.pdf_processor;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;


public class TestUtil {

    public static final String BASIC_TEXT_PDF = "basic-text-cleaned.pdf";
    private static final String USER_PASSWORD = "user123";

    public static MultipartFile getPdfFromResources(String resourcesPath, String outputFileName) throws Exception {
        InputStream fileInputStream = TestUtil.class.getClassLoader().getResourceAsStream(resourcesPath);
        return new MockMultipartFile("file", outputFileName, "application/pdf", fileInputStream);
    }

    public static String getTextContentFromResources(String resourcesPath) {
        try (InputStream fileInputStream = TestUtil.class.getClassLoader().getResourceAsStream(resourcesPath)) {
            if (fileInputStream == null) {
                throw new IOException("File not found");
            }
            return new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while trying to read pdf file from test resources.", e);
        }
    }

    public static <T> T runWithTestPDDocument(String testResourcesPath, Function<PDDocument, T> function) {
        try (var documentInputStream = Objects.requireNonNull(TestUtil.class.getClassLoader().getResourceAsStream(testResourcesPath));
             var rar = new RandomAccessReadBuffer(documentInputStream);
             var document = Loader.loadPDF(rar)) {
            return function.apply(document);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to read pdf file from test resources.", e);
        }
    }

    public static <T> T runWithTestPDDocument(MultipartFile file, Function<PDDocument, T> function) {
        try (var documentInputStream = file.getInputStream();
             var rar = new RandomAccessReadBuffer(documentInputStream);
             var document = Loader.loadPDF(rar, USER_PASSWORD)) {
            return function.apply(document);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to get input stream of file..", e);
        }
    }
}
