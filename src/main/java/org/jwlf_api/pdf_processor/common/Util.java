package org.jwlf_api.pdf_processor.common;

import lombok.extern.slf4j.Slf4j;
import org.jwlf_api.pdf_processor.content_extraction.FileEntry;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class Util {

    public static final int MULTIPLIER_MEGA = 1024 * 1024;

    private Util() {
    }

    public static StreamingResponseBody createZipFromFiles(List<FileEntry> files) {
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

    public static StreamingResponseBody createZipFromFilePaths(List<Path> pdfFiles) {
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
                forceDeleteDirectory(parent);
            }
        };
    }

    public static void forceDeleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            try (var paths = Files.walk(directory)) {
                paths.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                                //ignored
                            }
                        });
            }
        }
    }

}