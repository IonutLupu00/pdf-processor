package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfSecurityMetadata extends PdfMetadata{

    private boolean encrypted;
    private String filter;
    private boolean canPrint;
    private boolean canModify;
    private boolean canModifyAnnotations;
    private boolean canExtractContent;
    private boolean canExtractForAccessibility;
    private boolean canFillInForm;
    private boolean canAssembleDocument;
}
