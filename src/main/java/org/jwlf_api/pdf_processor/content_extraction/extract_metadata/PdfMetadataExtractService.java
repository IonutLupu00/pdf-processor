package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractException;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractService;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PdfMetadataExtractService extends PdfContentExtractService<Map<String, PdfMetadata>> {

    private final PdfMetadataExtractor pdfMetadataExtractor;

    public PdfMetadataExtractService(PdfMetadataExtractor pdfMetadataExtractor) {
        this.pdfMetadataExtractor = pdfMetadataExtractor;
    }

    protected Map<String, PdfMetadata> extract(PdfRequest request) throws PdfContentExtractException {
        PdfMetadataExtractRequest metadataExtractRequest = (PdfMetadataExtractRequest) request;
        try (RandomAccessRead rar = new RandomAccessReadBuffer(metadataExtractRequest.getFile().getInputStream());
             PDDocument document = Loader.loadPDF(rar)) {
            Set<PdfMetadataType> pdfMetadataTypes = metadataExtractRequest.getMetadataTypes().stream()
                    .map(PdfMetadataType::fromValue).collect(Collectors.toSet());
            return extractMetadata(pdfMetadataTypes, document);
        } catch (IOException e) {
            throw new PdfContentExtractException(e.getMessage(), e);
        }
    }

    private Map<String, PdfMetadata> extractMetadata(Set<PdfMetadataType> pdfMetadataTypes, PDDocument document) {
        return pdfMetadataTypes.stream()
                .collect(Collectors.toMap(
                        PdfMetadataType::getValue,
                        pdfMetadataType -> pdfMetadataExtractor.extractMetadata(pdfMetadataType, document)));
    }
}
