package org.jwlf_api.pdf_processor.split;

import org.jwlf_api.pdf_processor.common.PdfException;

public class PdfSplitException extends PdfException {

    public PdfSplitException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfSplitException(String message) {
        super(message);
    }
}
