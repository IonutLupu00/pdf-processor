package org.jwlf_api.pdf_processor.content_extraction;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.Util;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PdfTextExtractService extends PdfContentExtractService {

    @Override
    protected StreamingResponseBody extract(PdfRequest request) throws PdfContentExtractException {
        MultipartFile file = ((PdfContentExtractRequest) request).getFile();
        try (RandomAccessRead rar = new RandomAccessReadBuffer(file.getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            PDFTextStripper stripper = new PDFTextStripper();
            log.debug("Extracting text from PDF: {}", file.getOriginalFilename());
            String text = stripper.getText(document).trim();
            log.debug("Extracted text from PDF: {}", file.getOriginalFilename());
            FileEntry textFile = new FileEntry(Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\.pdf$", ".txt"), text.getBytes(StandardCharsets.UTF_8));
            List<FileEntry> files = Collections.singletonList(textFile);
            return Util.createZipFromFiles(files);
        } catch (IOException e) {
            throw new PdfContentExtractException("Error extracting text from PDF", e);
        }
    }
}
