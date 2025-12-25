package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PdfSignatureMetadata extends PdfMetadata{

    private boolean hasSignatures;
    private int signatureCount;
    private List<PdfSignatureInfo> signatures;

    @Getter
    @Setter
    public static class PdfSignatureInfo {
        private String name;
        private String location;
        private String reason;
        private String contactInfo;
        private Instant signingTime;
        private boolean coversWholeDocument;
    }
}
