package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;

public enum Trapped {
    TRUE("True"), FALSE("False"), UNKNOWN("Unknown");
    private final String value;
    Trapped(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static Trapped fromValue(String value) {
        return Trapped.valueOf(value.toUpperCase());
    }
}
