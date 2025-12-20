package org.jwlf_api.pdf_processor.content_extraction;

import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.PdfService;

@Slf4j
public abstract class PdfContentExtractService<T> extends PdfService<T> {

    protected abstract T extract(PdfRequest request) throws PdfContentExtractException;

    @Override
    public T processRequest(PdfRequest request) throws PdfContentExtractException {
        PdfContentExtractRequest pdfContentExtractRequest = (PdfContentExtractRequest) request;
        log.debug("Content extract request received with type {}.", pdfContentExtractRequest.getContentType());
        validateRequest(request);

        long startTime = System.nanoTime();
        T result = extract(request);
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

        if (pdfContentExtractRequest.getFile() == null) {
            throw new PdfContentExtractException("File is required");
        }
    }
}
