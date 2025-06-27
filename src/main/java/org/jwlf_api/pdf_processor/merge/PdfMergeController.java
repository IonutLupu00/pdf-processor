package org.jwlf_api.pdf_processor.merge;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@Slf4j
@RestController
@RequestMapping("/pdf/merge")
@AllArgsConstructor
public class PdfMergeController {

    PdfMergeService pdfMergeService;

    @PostMapping
    public ResponseEntity<StreamingResponseBody> merge(@ModelAttribute MergeRequest mergeRequest) throws PdfMergeException, InterruptedException {
        StreamingResponseBody resource = pdfMergeService.processRequest(mergeRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}