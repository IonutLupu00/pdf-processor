package org.jwlf_api.pdf_processor.common;

import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractException;
import org.jwlf_api.pdf_processor.merge.PdfMergeException;
import org.jwlf_api.pdf_processor.split.PdfSplitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String MESSAGE = "message";

    @ExceptionHandler(PdfMergeException.class)
    public ResponseEntity<Map<String, String>> handlePdfMergeException(PdfMergeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put(MESSAGE, ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(PdfSplitException.class)
    public ResponseEntity<Map<String, String>> handlePdfSplitException(PdfSplitException ex) {
        Map<String, String> body = new HashMap<>();
        body.put(MESSAGE, ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(PdfContentExtractException.class)
    public ResponseEntity<Map<String, String>> handleContentExtractException(PdfContentExtractException ex) {
        Map<String, String> body = new HashMap<>();
        body.put(MESSAGE, ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(PdfException.class)
    public ResponseEntity<Map<String, String>> handleGenericPdfException(PdfException ex) {
        Map<String, String> body = new HashMap<>();
        body.put(MESSAGE, ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put(MESSAGE, "An unexpected issue has occurred.");
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
