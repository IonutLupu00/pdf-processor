package org.jwlf_api.pdf_processor.common;

public class PdfException extends Exception {

    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfException(String message) {
        super(message);
    }
}
