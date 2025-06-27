package org.jwlf_api.pdf_processor.merge;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PdfMerger {

    private static final List<Integer> SUCCESS_EXIT_CODES = List.of(0, 3);

    private PdfMerger() {
    }

    protected static StreamingResponseBody mergePdfs(List<MultipartFile> files) throws IOException, InterruptedException {
        Path tempDir = Files.createTempDirectory("merged-pdfs-");
        List<Path> tempFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            Path tempFile = tempDir.resolve(UUID.randomUUID() + ".pdf");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFiles.add(tempFile);
        }

        Path output = tempDir.resolve("merged.pdf");
        int exitCode = mergeWithQpdf(tempFiles, output);
        if (!SUCCESS_EXIT_CODES.contains(exitCode)) {
            throw new IOException("qpdf failed with exit code " + exitCode);
        }

        for (Path path : tempFiles) {
            Files.deleteIfExists(path);
        }

        return outputStream -> {
            try (InputStream inputStream = Files.newInputStream(output)) {
                inputStream.transferTo(outputStream);
            } finally {
                Files.deleteIfExists(output);
                Files.deleteIfExists(tempDir);
            }
        };
    }

    private static int mergeWithQpdf(List<Path> tempFiles, Path output) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("qpdf");
        command.add("--empty");
        command.add("--pages");
        for (Path path : tempFiles) {
            command.add(path.toString());
        }
        command.add("--");
        command.add(output.toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.transferTo(Writer.nullWriter());
        }

        return process.waitFor();
    }
}
