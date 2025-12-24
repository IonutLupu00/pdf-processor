package org.jwlf_api.pdf_processor.content_extraction.extract_metadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractException;
import org.jwlf_api.pdf_processor.content_extraction.PdfContentExtractType;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfDocumentInformation;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadata;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType;
import org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfXmpCore;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jwlf_api.pdf_processor.TestDataGenerator.generateMockPdfFiles;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.DOCUMENT_INFO;
import static org.jwlf_api.pdf_processor.content_extraction.extract_metadata.data.PdfMetadataType.XMP_CORE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfMetadataExtractServiceTest {

    @InjectMocks
    private PdfMetadataExtractService pdfMetadataExtractService;

    @Mock
    private PdfMetadataExtractor pdfMetadataExtractor;

    @Test
    void testExtract_allMetadata_success() throws PdfContentExtractException {
        MultipartFile file = generateMockPdfFiles(1).getFirst();
        Set<String> metadataTypes = Set.of(
                DOCUMENT_INFO.getValue(),
                XMP_CORE.getValue()
        );
        PdfMetadataExtractRequest pdfMetadataExtractRequest = new PdfMetadataExtractRequest(file, PdfContentExtractType.METADATA, metadataTypes);

        mockPdfMetadataExtractor();
        Map<String, PdfMetadata> response = pdfMetadataExtractService.extract(pdfMetadataExtractRequest);

        assertEquals(metadataTypes, response.keySet());
    }

    private void mockPdfMetadataExtractor() {
        when(pdfMetadataExtractor.extractMetadata(any(PdfMetadataType.class), any(PDDocument.class)))
                .thenAnswer(invocation -> {
                    PdfMetadataType type = invocation.getArgument(0);

                    return switch (type) {
                        case DOCUMENT_INFO -> new PdfDocumentInformation(null);
                        case XMP_CORE -> PdfXmpCore.of(null);
                        default -> null;
                    };
                });
    }
}
