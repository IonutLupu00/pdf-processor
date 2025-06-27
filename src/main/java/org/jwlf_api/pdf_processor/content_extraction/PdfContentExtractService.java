package org.jwlf_api.pdf_processor.content_extraction;

import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.PdfService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
public abstract class PdfContentExtractService extends PdfService {
    
    protected abstract StreamingResponseBody extract(PdfRequest request) throws PdfContentExtractException;

    @Override
    public StreamingResponseBody processRequest(PdfRequest request) throws PdfContentExtractException {
        PdfContentExtractRequest pdfContentExtractRequest = (PdfContentExtractRequest) request;
        log.debug("Content extract request received with type {}.", pdfContentExtractRequest.getContentType());
        validateRequest(request);

        long startTime = System.nanoTime();
        StreamingResponseBody result = extract(request);
        long endTime = System.nanoTime();
        long diffMs = (endTime - startTime) / 1000000;
        log.debug("Operation completed in {} ms", diffMs);
        return result;
    }

    @Override
    protected void validateRequest(PdfRequest request) throws PdfContentExtractException {
        if (request == null) {
            throw new PdfContentExtractException("Request can't be null");
        }
        PdfContentExtractRequest pdfContentExtractRequest;
        pdfContentExtractRequest = (PdfContentExtractRequest) request;

        MultipartFile file = pdfContentExtractRequest.getFile();
        if (file == null) {
            throw new PdfContentExtractException("File is required");
        }
    }
}
