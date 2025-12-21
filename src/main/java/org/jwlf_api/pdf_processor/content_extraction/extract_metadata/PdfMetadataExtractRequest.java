package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import lombok.Getter;
import lombok.Setter;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractRequest;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
public class PdfMetadataExtractRequest extends PdfContentExtractRequest {

    private Set<String> metadataTypes;

    public PdfMetadataExtractRequest(MultipartFile file,  PdfContentExtractType contentType, Set<String> metadataTypes) {
        super(file, contentType);
        this.metadataTypes = metadataTypes;
    }
}
