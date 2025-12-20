package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractRequest;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PdfMetadataExtractRequest extends PdfContentExtractRequest {
    private Set<String> metadataTypes;
}
