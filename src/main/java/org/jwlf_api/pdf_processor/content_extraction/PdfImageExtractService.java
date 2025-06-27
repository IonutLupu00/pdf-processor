package org.jwlf_api.pdf_processor.content_extraction;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jwlf_api.pdf_processor.common.PdfRequest;
import org.jwlf_api.pdf_processor.common.Util;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PdfImageExtractService extends PdfContentExtractService {

    @Override
    protected StreamingResponseBody extract(PdfRequest request) throws PdfContentExtractException {
        MultipartFile file = ((PdfContentExtractRequest) request).getFile();
        log.debug("Processing document {}.", file.getName());
        try (RandomAccessRead rar = new RandomAccessReadBuffer(file.getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            List<FileEntry> pdfImages = new ArrayList<>();

            PDPageTree pages = document.getPages();
            int pageIndex = 0;
            for (PDPage page : pages) {
                pageIndex++;
                List<FileEntry> imagesFromPage = extractImagesFromPage(page.getResources(), pageIndex);
                pdfImages.addAll(imagesFromPage);
            }
            log.debug("Finished processing document {}.", file.getName());
            return Util.createZipFromFiles(pdfImages);
        } catch (IOException e) {
            throw new PdfContentExtractException("Error extracting images from PDF", e);
        }
    }

    private static List<FileEntry> extractImagesFromPage(PDResources resources, int pageIndex) throws IOException {
        log.debug("Extracting images from page {}", pageIndex);
        List<FileEntry> files = new ArrayList<>();
        int imageIndex = 0;
        for (COSName name : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(name);
            if (xObject instanceof PDImageXObject imageXObject) {
                imageIndex++;
                BufferedImage image = imageXObject.getImage();
                String imageFormat = imageXObject.getSuffix() != null ? imageXObject.getSuffix() : "png";
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, imageFormat, byteArrayOutputStream);
                String filename = "page_%s_image_%s.%s".formatted(pageIndex, imageIndex, imageFormat);
                files.add(new FileEntry(filename, byteArrayOutputStream.toByteArray()));
            }
        }
        return files;
    }
}
