package org.jwlf_api.pdf_processor.split;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@Slf4j
@RestController
@RequestMapping("/pdf/split")
@AllArgsConstructor
public class PdfSplitController {

    private final PdfSplitServiceFactory pdfSplitServiceFactory;

    @PostMapping
    public ResponseEntity<StreamingResponseBody> execute(@ModelAttribute SplitRequest request) throws PdfException {
        PdfSplitService pdfSplitService = pdfSplitServiceFactory.getPdfSplitService(request);
        StreamingResponseBody zippedPdfs = pdfSplitService.processRequest(request);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"files.zip\"")
                .body(zippedPdfs);
    }
}
