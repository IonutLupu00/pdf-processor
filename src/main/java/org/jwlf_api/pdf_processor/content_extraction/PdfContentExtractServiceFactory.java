package org.jwlf_api.pdf_processor.content_extraction;

import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.Metadata;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

@Component
public class PdfContentExtractServiceFactory {

    private final PdfContentExtractService<StreamingResponseBody> pdfImageExtractService;
    private final PdfContentExtractService<StreamingResponseBody> pdfTextExtractService;
    private final PdfContentExtractService<Map<String, Metadata>> pdfMetadataExtractService; ;

    public PdfContentExtractServiceFactory(PdfContentExtractService<StreamingResponseBody> pdfImageExtractService,
                                           PdfContentExtractService<StreamingResponseBody> pdfTextExtractService,
                                           PdfContentExtractService<Map<String, Metadata>> pdfMetadataExtractService) {
        this.pdfImageExtractService = pdfImageExtractService;
        this.pdfTextExtractService = pdfTextExtractService;
        this.pdfMetadataExtractService = pdfMetadataExtractService;
    }

    public PdfContentExtractService<?> resolveService(PdfContentExtractRequest request) throws PdfContentExtractException {
        PdfContentExtractType contentType = request.getContentType();
        if (contentType == null) {
            throw new PdfContentExtractException("Missing content type.");
        }
        return switch (contentType) {
            case TEXT -> pdfTextExtractService;
            case IMAGE -> pdfImageExtractService;
            case METADATA -> pdfMetadataExtractService;
            case OCR, ANNOTATION, TABLE, FORM -> null;
        };
    }

}
