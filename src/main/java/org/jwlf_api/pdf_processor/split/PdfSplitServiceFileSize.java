package org.jwlf_api.pdf_processor.split;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.jwlf_api.pdf_processor.common.PdfOptionsParser;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.util.StreamUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.jwlf_api.pdf_processor.split.PdfSplitter.splitByPageIndexesLeftLimit;

@Slf4j
@Service
@NoArgsConstructor
public class PdfSplitServiceFileSize extends PdfSplitService {

    @Override
    protected StreamingResponseBody splitPdf(SplitRequest request) throws PdfException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(request.getFile().getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            long splitByFileSize = getSplitByFileSize(request);
            List<Path> documents = splitDocument(document, splitByFileSize);
            return StreamUtil.createZipStreamFromFilePaths(documents);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to split PDF by file size.", e);
        }
    }

    @Override
    protected void validateRequest(PdfRequest request) throws PdfException {
        super.validateRequest(request);
        SplitRequest splitRequest;
        splitRequest = (SplitRequest) request;

        if (splitRequest.getSplitType() != SplitType.FILE_SIZE) {
            throw new PdfSplitException("Wrong split type. Expected [" + SplitType.FILE_SIZE + "].");
        }
        long splitByFileSize = getSplitByFileSize(splitRequest);
        long sourceFileSize = splitRequest.getFile().getSize();
        if (sourceFileSize <= splitByFileSize) {
            throw new PdfSplitException("Source file size [" + sourceFileSize + "] is smaller than the split file size [" + splitByFileSize + "]. Splitting by file size is not possible.");
        }
    }

    private static long getSplitByFileSize(SplitRequest request) throws PdfException {
        Map<SplitOption, String> options = PdfOptionsParser.parseOptions(request.getOptions());
        if (options == null) {
            throw new PdfSplitException("Request options are invalid. Please provide a correct file size.");
        }
        return Long.parseLong(options.get(SplitOption.FILE_SIZE_BYTES));
    }

    
    //TODO: consider rethinking this. Splitting twice may not be necessary, and currently the temp files are not cleaned from disk
    private List<Path> splitDocument(PDDocument document, long fileSizeLimit) throws IOException, PdfSplitException {
        List<Integer> allIndexes = IntStream.rangeClosed(1, document.getNumberOfPages()).boxed().sorted().toList();
        List<Path> singlePageDocuments = splitByPageIndexesLeftLimit(document, allIndexes);
        if (singlePageDocuments.size() == 1) {
            return singlePageDocuments;
        }
        List<Integer> finalIndexes = new ArrayList<>();
        long currentPartSize = 0;
        for (int i = 0; i < singlePageDocuments.size(); i++) {
            Path currentDoc = singlePageDocuments.get(i);
            long currentDocSize = Files.size(currentDoc);
            currentPartSize += currentDocSize;

            if (currentPartExceedsLimit(fileSizeLimit, currentPartSize, currentDocSize)) {
                if (i == 0) {
                    throwFileSizeLimitException(fileSizeLimit, currentPartSize, i);
                }
                finalIndexes.add(i);
                currentPartSize = currentDocSize;
            }

            if (i == singlePageDocuments.size() - 1) {
                if (currentPartSize <= fileSizeLimit) {
                    finalIndexes.add(i + 1);
                } else {
                    throwFileSizeLimitException(fileSizeLimit, currentPartSize, i);
                }
            }
        }

        return splitByPageIndexesLeftLimit(document, finalIndexes);
    }

    private static void throwFileSizeLimitException(long fileSizeLimit, long currentPartSize, int i) throws PdfSplitException {
        String errorMessage = "File size limit is too small. Reached [%s] at page [%s] with limit [%s].".formatted(currentPartSize, i + 1, fileSizeLimit);
        throw new PdfSplitException(errorMessage);
    }

    private static boolean currentPartExceedsLimit(long fileSizeLimit, long currentPartSize, long currentDocSize) {
        return currentPartSize > fileSizeLimit && currentPartSize - currentDocSize <= fileSizeLimit;
    }

}