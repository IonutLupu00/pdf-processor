package org.jwlf_api.pdf_processor.merge;


import org.jwlf_api.pdf_processor.common.PdfException;

public class PdfMergeException extends PdfException {

    public PdfMergeException(String errorMessage) {
        super(errorMessage);
    }

    public PdfMergeException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
