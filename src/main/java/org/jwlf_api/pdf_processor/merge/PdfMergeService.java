package org.jwlf_api.pdf_processor.merge;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.PdfService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

import static org.jwlf_api.pdf_processor.merge.PdfMerger.mergePdfs;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Service
@NoArgsConstructor
@Slf4j
public class PdfMergeService extends PdfService {

    @Override
    public StreamingResponseBody processRequest(PdfRequest request) throws PdfMergeException, InterruptedException {
        try {
            MergeRequest mergeRequest;
            mergeRequest = (MergeRequest) request;
            validateRequest(mergeRequest);

            log.debug("Merge request received with {} files.", mergeRequest.getFiles().size());
            long startTime = System.nanoTime();
            StreamingResponseBody result = mergePdfs(mergeRequest.getFiles());
            long endTime = System.nanoTime();
            long diffMs = (endTime - startTime) / 1000000;
            log.debug("Merge completed in {} ms", diffMs);
            return result;
        } catch (PdfMergeException e) {
            log.error("PDF merge failed due to validation error.", e);
            throw e;
        } catch (InterruptedException e) {
            log.error("Pdf merge operations was interrupted");
            throw e;
        } catch (Exception e) {
            log.error("PDF merge failed.", e);
            throw new PdfMergeException("Pdf merge failed.", e);
        }
    }

    @Override
    protected void validateRequest(PdfRequest request) throws PdfMergeException {
        MergeRequest mergeRequest;
        mergeRequest = (MergeRequest) request;

        List<MultipartFile> files = mergeRequest.getFiles();
        if (files == null || files.isEmpty()) {
            throw new PdfMergeException("No files provided.");
        }

        for (MultipartFile file : files) {
            if (!APPLICATION_PDF_VALUE.equalsIgnoreCase(file.getContentType())) {
                throw new PdfMergeException("Invalid file type for [" + file.getName() + "]. Only PDF files are allowed.");
            }

            if (file.isEmpty()) {
                throw new PdfMergeException("Empty file detected: [" + file.getName() + "]");
            }

        }
    }
}
