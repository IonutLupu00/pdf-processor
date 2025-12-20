package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import lombok.Getter;

public enum MetadataType {
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

    MetadataType(String value) {
        this.value = value;
    }
}
