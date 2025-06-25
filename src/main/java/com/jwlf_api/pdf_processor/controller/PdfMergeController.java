package com.jwlf_api.pdf_processor.controller;

import com.jwlf_api.pdf_processor.service.merge.PdfMergeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pdf/merge")
@AllArgsConstructor
public class PdfMergeController {

    PdfMergeService pdfMergeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> mergePdfs(@RequestParam("files") List<MultipartFile> files)  {
        try {
            byte[] result = pdfMergeService.merge(files);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ignored) {

        }
        return null;
    }
}