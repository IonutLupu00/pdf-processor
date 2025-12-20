package org.jwlf_api.pdf_processor.content_extraction;

import org.jwlf_api.pdf_processor.common.PdfException;

public class PdfContentExtractException extends PdfException {

    public PdfContentExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfContentExtractException(String message) {
        super(message);
    }
}
