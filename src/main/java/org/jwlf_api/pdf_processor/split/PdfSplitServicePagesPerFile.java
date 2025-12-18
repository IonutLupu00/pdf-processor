package org.jwlf_api.pdf_processor.split;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.jwlf_api.pdf_processor.common.PdfOptionsParser;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jwlf_api.pdf_processor.common.StreamUtil.createZipStreamFromFilePaths;
import static org.jwlf_api.pdf_processor.split.PdfSplitter.splitByPageIndexesLeftLimit;

@Slf4j
@Service
public class PdfSplitServicePagesPerFile extends PdfSplitService {


    @Override
    protected StreamingResponseBody splitPdf(SplitRequest request) throws PdfException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(request.getFile().getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            Map<SplitOption, String> options = PdfOptionsParser.parseOptions(request.getOptions());
            int pagesPerFile = Integer.parseInt(options.get(SplitOption.PAGES_PER_FILE));
            if (pagesPerFile > document.getNumberOfPages()) {
                throw new PdfSplitException("Pages per file must be less than or equal to the number of pages in the document.");
            }

            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < document.getNumberOfPages(); i += pagesPerFile) {
                indexes.add(i + 1);
            }

            List<Path> documents = splitByPageIndexesLeftLimit(document, indexes);
            return createZipStreamFromFilePaths(documents);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to load PDF document.", e);
        }
    }

    @Override
    protected void validateRequest(PdfRequest request) throws PdfException {
        super.validateRequest(request);
        SplitRequest splitRequest;
        splitRequest = (SplitRequest) request;

        Map<SplitOption, String> requestOptions = PdfOptionsParser.parseOptions(splitRequest.getOptions());
        int pagesPerFile = Integer.parseInt(requestOptions.get(SplitOption.PAGES_PER_FILE));
        log.debug("Pages per file: {}", pagesPerFile);

        if (pagesPerFile <= 0) {
            throw new PdfSplitException("Pages per file argument value must be greater than zero.");
        }
    }

}