package org.jwlf_api.pdf_processor.common;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RefreshScope
public abstract class PdfService {

    protected abstract void validateRequest(PdfRequest request) throws PdfException;

    public abstract StreamingResponseBody processRequest(PdfRequest request) throws PdfException, InterruptedException;
}
