package org.jwlf_api.pdf_processor.split;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jwlf_api.pdf_processor.common.Util;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.jwlf_api.pdf_processor.split.PdfSplitter.splitByPageIndexesRightLimit;

@Service
@Slf4j
public class PdfSplitServiceCode extends PdfSplitService {

    private static final int DPI = 250;

    @Override
    protected StreamingResponseBody splitPdf(SplitRequest request) throws PdfSplitException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(request.getFile().getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            List<Path> documents = splitDocument(document);
            return Util.createZipFromFilePaths(documents);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to split PDF by code.", e);
        }
    }

    private List<Path> splitDocument(PDDocument document) throws IOException, PdfSplitException {
        List<Integer> pageIndexesWithCode = getPagesWithAnyCode(document);
        return splitByPageIndexesRightLimit(document, pageIndexesWithCode);
    }

    private List<Integer> getPagesWithAnyCode(PDDocument document) throws IOException {
        PDFRenderer renderer = new PDFRenderer(document);
        List<Integer> pageIndexesWithCode = new ArrayList<>();

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage pageImage = renderer.renderImageWithDPI(i, DPI);

            Path tempImage = Files.createTempFile("pdf_page_", ".png");
            ImageIO.write(pageImage, "PNG", tempImage.toFile());

            try {
                if (pageContainsAnyCode(tempImage)) {
                    pageIndexesWithCode.add(i + 1);
                }
            } finally {
                Files.deleteIfExists(tempImage); // cleanup
            }
        }

        return pageIndexesWithCode;
    }

    private boolean pageContainsAnyCode(Path pageImage) throws IOException {
        BufferedImage image = ImageIO.read(pageImage.toFile());
        if (image == null) {
            throw new IOException("Failed to load image: " + pageImage);
        }

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        MultiFormatReader reader = new MultiFormatReader();
        GenericMultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(reader);

        try {
            Result[] results = multiReader.decodeMultiple(bitmap);
            return results != null && results.length > 0;
        } catch (NotFoundException e) {
            return false;
        }
    }
}
