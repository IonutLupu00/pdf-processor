package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PdfEmbeddedFilesMetadata extends PdfMetadata {

    private List<EmbeddedFileMetadata> embeddedFileMetadata;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class EmbeddedFileMetadata {
        private String filename;
        private String mimeType;
        private long size;
    }
}
