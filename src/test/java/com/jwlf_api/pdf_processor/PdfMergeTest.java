package com.jwlf_api.pdf_processor;

import com.jwlf_api.pdf_processor.service.merge.PdfMergeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PdfMergeTest {

    PdfMergeService pdfMergeService;

    @BeforeEach
    void setup() {
        pdfMergeService = new PdfMergeService();
    }

    @Test
    void mergeSimplePdfs() throws IOException {
        InputStream input1 = new ClassPathResource("pdf_merge/file-example_PDF_1MB.pdf").getInputStream();
        InputStream input2 = new ClassPathResource("pdf_merge/file-example_PDF_500_kB.pdf").getInputStream();

        MultipartFile multipartFile1 = new MockMultipartFile("pdf", input1);
        MultipartFile multipartFile2 = new MockMultipartFile("pdf2", input2);

        List<MultipartFile> multipartFiles = List.of(multipartFile1, multipartFile2);

        byte[] result = pdfMergeService.merge(multipartFiles);
        Path output = Paths.get("src/test/resources/pdf_merge/expected/merged-pdfs.pdf");
        Files.write(output, result);
    }
}
