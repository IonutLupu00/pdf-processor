package com.jwlf_api.pdf_processor.service.merge;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PdfMergeService implements PdfMerger {

    @Override
    public byte[] merge(List<MultipartFile> files) {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        return null;
    }
}