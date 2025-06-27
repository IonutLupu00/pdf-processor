package org.jwlf_api.pdf_processor.split;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class PdfSplitter {

    private static final List<Integer> SUCCESS_EXIT_CODES = List.of(0, 3);

    private PdfSplitter() {
    }

    protected static List<Path> splitByPageRanges(PDDocument document, List<PageRange> pageRanges) throws PdfSplitException {
        try {
            Path tempDir = Files.createTempDirectory("pdfsplit_");
            log.debug("Splitting document [{}] into ranges {} inside temporary directory [{}].", document.getDocument().getDocumentID(), pageRanges, tempDir.toString());
            Path inputFile = tempDir.resolve("input.pdf");
            document.save(inputFile.toFile());
            return splitPdfFileByRanges(pageRanges, inputFile);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PdfSplitException("Splitting interrupted", e);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to split PDF with qpdf", e);
        }
    }

    protected static List<Path> splitByPageRanges(Path inputFile, List<PageRange> pageRanges) throws PdfSplitException {
        if (!Files.exists(inputFile)) {
            throw new IllegalArgumentException("Input file shouldn't be null");
        }

        try {
            Path tempDir = inputFile.getParent();
            log.debug("Splitting document from path [{}] into ranges {} inside temporary directory [{}].", inputFile.toAbsolutePath(), pageRanges, tempDir.toString());
            return splitPdfFileByRanges(pageRanges, inputFile);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PdfSplitException("Splitting interrupted", e);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to split PDF with qpdf", e);
        }
    }

    private static List<Path> splitPdfFileByRanges(List<PageRange> pageRanges, Path inputFile) throws PdfSplitException, IOException, InterruptedException {
        if (pageRangesOverlap(pageRanges)) {
            throw new PdfSplitException("Page ranges overlap");
        }

        Path tempDir = inputFile.getParent();

        List<Path> splitDocumentsPaths = new ArrayList<>();
        for (int i = 0; i < pageRanges.size(); i++) {
            PageRange range = pageRanges.get(i);
            Integer start = range.getStart();
            Integer end = range.getEnd();
            validatePageRange(start, end);

            String rangeString = start.equals(end) ? start.toString() : "%s-%s".formatted(start, end);
            Path outputFilePath = tempDir.resolve("split-" + (i + 1) + ".pdf");
            int exitCode = splitWithQpdf(inputFile, rangeString, outputFilePath);
            if (!SUCCESS_EXIT_CODES.contains(exitCode)) {
                throw new PdfSplitException("qpdf failed for range: " + rangeString);
            }

            splitDocumentsPaths.add(outputFilePath);
        }
        log.debug("Splitting completed. Total split documents: {}", splitDocumentsPaths.size());
        return splitDocumentsPaths;
    }

    private static int splitWithQpdf(Path inputFile, String rangeString, Path outputFilePath) throws IOException, InterruptedException {
        List<String> command = List.of("qpdf", inputFile.toString(), "--pages", ".", rangeString, "--", outputFilePath.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.transferTo(Writer.nullWriter());
        }

        return process.waitFor();
    }

    protected static List<Path> splitByPageIndexesRightLimit(PDDocument document, List<Integer> pageIndexes) throws PdfSplitException {
        return splitByPageRanges(document, getPageRangesRightLimit(pageIndexes));
    }

    protected static List<Path> splitByPageIndexesRightLimit(Path inputFile, List<Integer> pageIndexes) throws PdfSplitException {
        return splitByPageRanges(inputFile, getPageRangesRightLimit(pageIndexes));
    }

    //Each index is the end of a range (inclusive), starting from the index 1, thus limiting the ranges to the right
    private static List<PdfSplitter.PageRange> getPageRangesRightLimit(List<Integer> pageIndexes) {
        if (pageIndexes == null || pageIndexes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PageRange> ranges = new ArrayList<>();
        for (int i = 0; i < pageIndexes.size(); i++) {
            int current = pageIndexes.get(i);
            int previous = i > 0 ? pageIndexes.get(i) : 1;
            PageRange pageRange = new PageRange(previous, current);
            ranges.add(pageRange);
        }

        return ranges;
    }

    protected static List<Path> splitByPageIndexesLeftLimit(PDDocument document, List<Integer> pageIndexes) throws PdfSplitException {
        return splitByPageRanges(document, getPageRangesLeftLimit(pageIndexes, document.getNumberOfPages()));
    }

    protected static List<Path> splitByPageIndexesLeftLimit(Path inputFile, List<Integer> pageIndexes, int totalPagesCount) throws PdfSplitException {
        return splitByPageRanges(inputFile, getPageRangesLeftLimit(pageIndexes, totalPagesCount));
    }

    //Each index is the start of a range (inclusive), until the last page of the document, thus limiting the ranges to the left
    private static List<PdfSplitter.PageRange> getPageRangesLeftLimit(List<Integer> pageIndexes, Integer totalPagesCount) {
        if (pageIndexes == null || pageIndexes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PageRange> ranges = new ArrayList<>();
        for (int i = 0; i < pageIndexes.size(); i++) {
            int current = pageIndexes.get(i);
            int next = i + 1 < pageIndexes.size() ? pageIndexes.get(i + 1) - 1 : totalPagesCount;
            PageRange pageRange = new PageRange(current, next);
            ranges.add(pageRange);
        }

        return ranges;
    }

    private static void validatePageRange(int start, int end) throws PdfSplitException {
        if (start > end) {
            String errorMessage = "Invalid page range: " + start + "-" + end;
            log.error(errorMessage);
            throw new PdfSplitException(errorMessage);
        }
    }

    protected static boolean pageRangesOverlap(List<PageRange> ranges) {
        List<PageRange> sortedRanges = ranges.stream().sorted(Comparator.comparingInt(r -> Math.min(r.getStart(), r.getEnd()))).toList();

        for (int i = 1; i < sortedRanges.size(); i++) {
            PageRange prev = sortedRanges.get(i - 1);
            PageRange curr = sortedRanges.get(i);

            if (curr.getStart() <= prev.getEnd()) {
                return true;
            }
        }
        return false;
    }

    @AllArgsConstructor
    @Data
    public static class PageRange {
        Integer start;
        Integer end;

        @Override
        public String toString() {
            return "start=" + start + ", end=" + end;
        }
    }
}
