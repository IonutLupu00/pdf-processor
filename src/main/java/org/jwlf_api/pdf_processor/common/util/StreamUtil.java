package org.jwlf_api.pdf_processor.common.util;

import org.jwlf_api.pdf_processor.common.FileEntry;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class StreamUtil {

    private StreamUtil() {
    }

    public static StreamingResponseBody createZipStreamFromFiles(List<FileEntry> files) {
        return outputStream -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
                for (FileEntry file : files) {
                    ZipEntry zipEntry = new ZipEntry(file.getFileName());
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(file.getContent());
                    zipOutputStream.closeEntry();
                }
            }
        };
    }

    public static StreamingResponseBody createZipStreamFromFilePaths(List<Path> pdfFiles) {
        if (CollectionUtils.isEmpty(pdfFiles)) {
            return outputStream -> {
            };
        }
        return outputStream -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
                for (int i = 0; i < pdfFiles.size(); i++) {
                    Path pdfFile = pdfFiles.get(i);
                    ZipEntry entry = new ZipEntry("part_" + (i + 1) + ".pdf");
                    zipOutputStream.putNextEntry(entry);
                    Files.copy(pdfFile, zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            } finally {
                Path parent = pdfFiles.getFirst().getParent();
                FileUtil.forceDeleteDirectory(parent);
            }
        };
    }
}