package com.jwlf_api.pdf_processor.service.merge;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PdfMerger {
    byte[] merge(List<MultipartFile> files);
}
