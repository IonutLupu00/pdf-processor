package org.jwlf_api.pdf_processor.split;

import lombok.Getter;
import lombok.Setter;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SplitRequest implements PdfRequest {
    private MultipartFile file;
    private SplitType splitType;
    private String options;

    public SplitRequest() {
    }

    public SplitRequest(MultipartFile file, SplitType splitType, String options) {
        this.file = file;
        this.splitType = splitType;
        this.options = options;
    }

    private SplitRequest(Builder builder) {
        this.file = builder.file;
        this.splitType = builder.splitType;
        this.options = builder.options;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MultipartFile file;
        private SplitType splitType;
        private String options;

        public Builder file(MultipartFile file) {
            this.file = file;
            return this;
        }

        public Builder splitType(SplitType splitType) {
            this.splitType = splitType;
            return this;
        }

        public Builder options(String options) {
            this.options = options;
            return this;
        }

        public SplitRequest build() {
            return new SplitRequest(this);
        }
    }
}