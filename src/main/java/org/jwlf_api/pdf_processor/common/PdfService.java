package org.jwlf_api.pdf_processor.common;

import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
public abstract class PdfService<T> {

    protected abstract void validateRequest(PdfRequest request) throws PdfException;

    public abstract T processRequest(PdfRequest request) throws PdfException, InterruptedException;
}
