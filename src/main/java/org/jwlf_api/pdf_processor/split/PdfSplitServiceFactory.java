package org.jwlf_api.pdf_processor.split;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PdfSplitServiceFactory {

    private final PdfSplitService pdfSplitServicePagesPerFile;
    private final PdfSplitService pdfSplitServiceTotalFiles;
    private final PdfSplitService pdfSplitServiceBookmark;
    private final PdfSplitService pdfSplitServicePageRanges;
    private final PdfSplitService pdfSplitServiceFileSize;
    private final PdfSplitService pdfSplitServiceCode;

    public PdfSplitServiceFactory(@Qualifier("pdfSplitServicePagesPerFile") PdfSplitService pdfSplitServicePagesPerFile,
                                  @Qualifier("pdfSplitServiceTotalFiles") PdfSplitService pdfSplitServiceTotalFiles,
                                  @Qualifier("pdfSplitServiceBookmark") PdfSplitService pdfSplitServiceBookmark,
                                  @Qualifier("pdfSplitServicePageRanges") PdfSplitService pdfSplitServicePageRanges,
                                  @Qualifier("pdfSplitServiceFileSize") PdfSplitService pdfSplitServiceFileSize,
                                  @Qualifier("pdfSplitServiceCode") PdfSplitService pdfSplitServiceCode) {
        this.pdfSplitServiceBookmark = pdfSplitServiceBookmark;
        this.pdfSplitServiceTotalFiles = pdfSplitServiceTotalFiles;
        this.pdfSplitServicePagesPerFile = pdfSplitServicePagesPerFile;
        this.pdfSplitServicePageRanges = pdfSplitServicePageRanges;
        this.pdfSplitServiceFileSize = pdfSplitServiceFileSize;
        this.pdfSplitServiceCode = pdfSplitServiceCode;
    }

    public PdfSplitService getPdfSplitService(SplitRequest request) throws PdfSplitException {
        if (request.getSplitType() == null) {
            throw new PdfSplitException("'splitType' is required in the request body.");
        }
        return switch (request.getSplitType()) {
            case PAGES_PER_FILE -> pdfSplitServicePagesPerFile;
            case TOTAL_FILES -> pdfSplitServiceTotalFiles;
            case BOOKMARK -> pdfSplitServiceBookmark;
            case PAGE_RANGES -> pdfSplitServicePageRanges;
            case FILE_SIZE -> pdfSplitServiceFileSize;
            case CODE -> pdfSplitServiceCode;
        };
    }
}