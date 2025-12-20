package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractException;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractService;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PdfMetadataExtractService extends PdfContentExtractService {

    private final MetadataExtractor metadataExtractor;

    public PdfMetadataExtractService(MetadataExtractor metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }

    @Override
    protected StreamingResponseBody extract(PdfRequest request) throws PdfContentExtractException {
        PdfMetadataExtractRequest metadataExtractRequest = (PdfMetadataExtractRequest) request;
        //TODO: centralize reading pdfs from the request.
        // Only load whole pdfs in memory under a certain size. Save temp on disk above that size.
        try (RandomAccessRead rar = new RandomAccessReadBuffer(metadataExtractRequest.getFile().getInputStream());
             PDDocument document = Loader.loadPDF(rar)) {

        } catch (IOException e) {
            throw new PdfContentExtractException(e.getMessage(), e);
        }
        return null;
    }

    Map<MetadataType, Metadata> extractMetadata(Set<MetadataType> includes, PDDocument document) {
        return includes.stream()
                .collect(Collectors.toMap(
                        metadataType -> metadataType,
                        metadataType -> metadataExtractor.extractMetadata(metadataType, document)));
    }
}
