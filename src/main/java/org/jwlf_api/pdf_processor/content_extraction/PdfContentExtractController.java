package org.jwlf_api.pdf_processor.content_extraction;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@RestController
@RequestMapping("/pdf/extract")
@AllArgsConstructor
public class PdfContentExtractController {

    PdfContentExtractServiceFactory pdfContentExtractServiceFactory;

    @PostMapping
    public ResponseEntity<StreamingResponseBody> execute(@ModelAttribute PdfContentExtractRequest request) throws PdfContentExtractException {
        PdfContentExtractService service = pdfContentExtractServiceFactory.resolveService(request);
        StreamingResponseBody zippedFiles = service.processRequest(request);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"images.zip\"")
                .body(zippedFiles);
    }
}
