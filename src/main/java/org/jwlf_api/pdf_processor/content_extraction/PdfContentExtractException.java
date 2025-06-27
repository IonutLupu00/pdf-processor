package org.jwlf_api.pdf_processor.content_extraction;

import org.jwlf_api.pdf_processor.common.PdfException;

public class PdfContentExtractException extends PdfException {

    protected PdfContentExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    protected PdfContentExtractException(String message) {
        super(message);
    }
}
