package org.jwlf_api.pdf_processor.split;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.jwlf_api.pdf_processor.common.PdfOptionsParser;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jwlf_api.pdf_processor.common.util.StreamUtil.createZipStreamFromFilePaths;
import static org.jwlf_api.pdf_processor.split.PdfSplitter.PageRange;
import static org.jwlf_api.pdf_processor.split.PdfSplitter.splitByPageRanges;

@Slf4j
@Service
public class PdfSplitServicePageRanges extends PdfSplitService {

    @Override
    protected StreamingResponseBody splitPdf(SplitRequest request) throws PdfException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(request.getFile().getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            List<PageRange> pageRanges = getPageRanges(request);
            List<Path> documents = splitByPageRanges(document, pageRanges);
            return createZipStreamFromFilePaths(documents);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to load PDF document.", e);
        }
    }

    private List<PageRange> getPageRanges(SplitRequest request) throws PdfException {
        Map<SplitOption, String> options = PdfOptionsParser.parseOptions(request.getOptions());
        if (options == null || !options.containsKey(SplitOption.PAGE_RANGES)) {
            throw new PdfSplitException("Page ranges option is missing.");
        }

        return parsePageRanges(options.get(SplitOption.PAGE_RANGES));
    }

    private List<PageRange> parsePageRanges(String rangesStr) throws PdfSplitException {
        if (rangesStr == null || rangesStr.isBlank()) {
            throw new PdfSplitException("Page ranges option is missing.");
        }

        List<PageRange> ranges = new ArrayList<>();
        for (String part : rangesStr.split(",")) {
            part = part.trim();
            if (part.isEmpty()) continue;

            String[] bounds = part.split("-");
            try {
                int start = Integer.parseInt(bounds[0]);
                int end = (bounds.length > 1) ? Integer.parseInt(bounds[1]) : start;

                if (start <= 0 || end < start) {
                    throw new PdfSplitException("Invalid page range: " + part);
                }

                ranges.add(new PageRange(start, end));
            } catch (NumberFormatException e) {
                throw new PdfSplitException("Invalid page range format: " + part, e);
            }
        }
        return ranges;
    }
}