package org.jwlf_api.pdf_processor.content_extraction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PdfContentExtractServiceFactory {

    private final PdfContentExtractService imageExtractService;
    private final PdfContentExtractService textExtractService;

    public PdfContentExtractServiceFactory(@Qualifier("pdfImageExtractService") PdfContentExtractService imageExtractService,
                                           @Qualifier("pdfTextExtractService") PdfContentExtractService textExtractService) {
        this.imageExtractService = imageExtractService;
        this.textExtractService = textExtractService;
    }

    public PdfContentExtractService resolveService(PdfContentExtractRequest request) throws PdfContentExtractException {
        PdfExtractContentType contentType = request.getContentType();
        if (contentType == null) {
            throw new PdfContentExtractException("Missing content type.");
        }
        return switch (contentType) {
            case TEXT -> textExtractService;
            case IMAGE -> imageExtractService;
            case OCR, ANNOTATION, METADATA, TABLE, FORM -> null;
        };
    }

}
