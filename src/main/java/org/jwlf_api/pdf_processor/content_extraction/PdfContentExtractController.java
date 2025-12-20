package org.jwlf_api.pdf_processor.content_extraction;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/pdf/extract")
@AllArgsConstructor
public class PdfContentExtractController {

    private final PdfContentExtractServiceFactory pdfContentExtractServiceFactory;

    @PostMapping
    public ResponseEntity<?> execute(@ModelAttribute PdfContentExtractRequest request) throws PdfContentExtractException {
        PdfContentExtractService service = pdfContentExtractServiceFactory.resolveService(request);
        Object serviceResponse = service.processRequest(request);
        switch (serviceResponse) {
            case StreamingResponseBody responseBody -> {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"images.zip\"")
                        .body(responseBody);
            }
            case Map responseBody -> {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"images.zip\"")
                        .body(responseBody);
            }
            default -> throw new IllegalArgumentException("Unexpected service return type");
        }
    }
}
