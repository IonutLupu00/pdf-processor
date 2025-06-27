package org.jwlf_api.pdf_processor.split;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jwlf_api.pdf_processor.common.Util.createZipFromFilePaths;
import static org.jwlf_api.pdf_processor.split.PdfSplitter.splitByPageIndexesLeftLimit;

@Slf4j
@Service
public class PdfSplitServiceTotalFiles extends PdfSplitService {

    @Override
    protected StreamingResponseBody splitPdf(SplitRequest request) throws PdfException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(request.getFile().getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            Map<SplitOption, String> options = parseOptions(request.getOptions());
            int totalFiles = Integer.parseInt(options.get(SplitOption.TOTAL_FILES));

            validate(totalFiles, document);

            int pagesPerFile = (int) Math.ceil((double) document.getNumberOfPages() / totalFiles);
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < document.getNumberOfPages(); i += pagesPerFile) {
                indexes.add(i + 1);
            }

            List<Path> documents = splitByPageIndexesLeftLimit(document, indexes);
            return createZipFromFilePaths(documents);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to load PDF document.", e);
        }
    }

    private static void validate(int totalFiles, PDDocument document) throws PdfSplitException {
        if (totalFiles > document.getNumberOfPages()) {
            throw new PdfSplitException("Total files must be less than or equal to the number of pages in the input PDF.");
        }
        if (totalFiles < 1) {
            throw new PdfSplitException("Total files must be greater than 0.");
        }
    }
}