package org.jwlf_api.pdf_processor.content_extraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PdfContentExtractRequest implements PdfRequest {
    private MultipartFile file;
    private String options;
    private PdfContentExtractType contentType;
}
