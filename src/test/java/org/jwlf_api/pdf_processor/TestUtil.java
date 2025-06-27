package org.jwlf_api.pdf_processor;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtil {

    public static MultipartFile getPdfFromResources(String resourcesPath, String outputFileName) throws Exception {
        InputStream fileInputStream = TestUtil.class.getClassLoader().getResourceAsStream(resourcesPath);
        return new MockMultipartFile("file", outputFileName, "application/pdf", fileInputStream);
    }

    public static String getTextContentFromResources(String resourcesPath) throws IOException {
        try (InputStream fileInputStream = TestUtil.class.getClassLoader().getResourceAsStream(resourcesPath)) {
            if (fileInputStream == null) {
                throw new IOException("File not found");
            }
            return new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
