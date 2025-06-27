package org.jwlf_api.pdf_processor.split;

import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.PdfService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Slf4j
public abstract class PdfSplitService extends PdfService {

    protected abstract StreamingResponseBody splitPdf(SplitRequest request) throws PdfException;

    @Override
    public StreamingResponseBody processRequest(PdfRequest request) throws PdfException {
        SplitRequest splitRequest;
        splitRequest = (SplitRequest) request;

        validateRequest(splitRequest);
        log.debug("Split request received of type [{}] with options [{}].", splitRequest.getSplitType(), splitRequest.getOptions());
        long startTime = System.nanoTime();
        StreamingResponseBody result = splitPdf(splitRequest);
        long endTime = System.nanoTime();
        long diffMs = (endTime - startTime) / 1000000;
        log.debug("Split completed in {} ms", diffMs);
        return result;
    }

    @Override
    protected void validateRequest(PdfRequest request) throws PdfException {
        SplitRequest splitRequest;
        splitRequest = (SplitRequest) request;

        if (splitRequest.getFile() == null) {
            throw new PdfSplitException("Pdf file is  required.");
        }

        MultipartFile file = splitRequest.getFile();
        if (file.isEmpty()) {
            log.error("Empty file detected: [{}]", file.getName());
            throw new PdfSplitException("Empty file detected: [" + file.getName() + "]");
        }

        if (!APPLICATION_PDF_VALUE.equalsIgnoreCase(file.getContentType())) {
            log.error("Invalid file type: [{}], Expected: {}", file.getContentType(), APPLICATION_PDF_VALUE);
            throw new PdfSplitException("Invalid file type for [" + file.getName() + "]. Only PDF files are allowed.");
        }
    }
}