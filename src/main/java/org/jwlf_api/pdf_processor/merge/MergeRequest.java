package org.jwlf_api.pdf_processor.merge;

import lombok.Getter;
import lombok.Setter;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class MergeRequest implements PdfRequest {
    private List<MultipartFile> files;
}
