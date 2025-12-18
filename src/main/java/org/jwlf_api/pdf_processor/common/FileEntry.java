package org.jwlf_api.pdf_processor.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class FileEntry {

    private String fileName;
    private byte[] content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileEntry other)) return false;
        return fileName.equals(other.fileName) &&
                Arrays.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "FileEntry{" +
                "fileName='" + fileName + '\'' +
                ", content=" + (content != null ? ("byte[" + content.length + "]") : "null") +
                '}';
    }
}
