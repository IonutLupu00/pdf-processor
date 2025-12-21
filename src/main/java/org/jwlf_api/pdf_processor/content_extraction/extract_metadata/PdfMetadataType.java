package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import lombok.Getter;

public enum PdfMetadataType {
    DOCUMENT_INFO("document-info"),
    XMP_CORE("xmp-core"),
    XMP_COMPLIANCE("xmp-compliance"),
    EMBEDDED_FILES("embedded-files"),
    SECURITY("security"),
    SIGNATURES("signatures"),
    TECHNICAL("technical"),
    PAGES("pages"),
    RAW_XMP("raw-xmp");

    @Getter
    private final String value;

    PdfMetadataType(String value) {
        this.value = value;
    }

    public static PdfMetadataType fromValue(String value) {
        for (PdfMetadataType type : PdfMetadataType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PdfMetadataType value: " + value);
    }
}
