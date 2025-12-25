package org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfPagesMetadata extends PdfMetadata {

    private int numberOfPages;
    private List<PdfPageMetadata> pages;

    @Getter
    @Setter
    public static class PdfPageMetadata {
        private int pageNumber;
        private float width;
        private float height;
        private int rotation;
    }

}
