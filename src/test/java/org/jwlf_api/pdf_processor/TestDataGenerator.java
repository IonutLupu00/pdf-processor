package org.jwlf_api.pdf_processor;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static javax.imageio.ImageIO.write;

public class TestDataGenerator {

    private TestDataGenerator() {
    }

    public static List<MultipartFile> generateMockPdfFilesWithMetadata(int count) {
        List<MultipartFile> files = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();

            try (PDDocument doc = new PDDocument()) {
                doc.addPage(new PDPage());

                PDDocumentInformation info = new PDDocumentInformation();
                info.setTitle("Title-" + UUID.randomUUID());
                info.setAuthor("Author-" + i);
                info.setSubject("Subject-" + i);
                info.setKeywords("key" + i);
                info.setCreator("MockGenerator");
                info.setProducer("PDFBox");
                info.setCreationDate(Calendar.getInstance());
                info.setModificationDate(Calendar.getInstance());

                doc.setDocumentInformation(info);

                XMPMetadata xmp = XMPMetadata.createXMPMetadata();

                DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
                dc.setTitle("xmp-title-" + UUID.randomUUID());
                dc.addCreator("xmp-author-" + i);
                dc.setDescription("xmp-description-" + i);
                dc.addSubject("xmp-subject-" + i);

                XMPBasicSchema xmpBasic = xmp.createAndAddXMPBasicSchema();
                xmpBasic.setCreatorTool("MockGenerator");
                xmpBasic.setCreateDate(GregorianCalendar.getInstance());
                xmpBasic.setModifyDate(GregorianCalendar.getInstance());
                xmpBasic.setMetadataDate(GregorianCalendar.getInstance());

                AdobePDFSchema pdfSchema = xmp.createAndAddAdobePDFSchema();
                pdfSchema.setProducer("PDFBox");
                pdfSchema.setKeywords(info.getKeywords());

                PDMetadata metadata = new PDMetadata(doc);
                ByteArrayOutputStream xmpOutput = new ByteArrayOutputStream();
                new XmpSerializer().serialize(xmp, xmpOutput, true);
                metadata.importXMPMetadata(xmpOutput.toByteArray());
                doc.getDocumentCatalog().setMetadata(metadata);

                doc.save(pdfOutput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }

            byte[] content = pdfOutput.toByteArray();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test-" + i + ".pdf",
                    "application/pdf",
                    content
            );
            files.add(file);
        }

        return files;
    }


    public static List<MultipartFile> generateMockPdfFiles(int count) {
        List<MultipartFile> files = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
            try (PDDocument doc = new PDDocument()) {
                doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
                doc.save(pdfOutput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] content = pdfOutput.toByteArray();
            MockMultipartFile file = new MockMultipartFile("file", "test-" + i + ".pdf", "application/pdf", content);
            files.add(file);
        }

        return files;
    }

    public static List<MultipartFile> generateMockPdfFilesWithPages(int count, int pagesPerDocument) {
        List<MultipartFile> files = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            byte[] content = createPdfContent(pagesPerDocument);
            MockMultipartFile file = new MockMultipartFile("file", "test-" + i + ".pdf", "application/pdf", content);
            files.add(file);
        }

        return files;
    }

    public static List<MultipartFile> generateMockPdfFilesWithTotalSize(int count, long totalSize) {
        List<MultipartFile> files = new ArrayList<>();

        long remainingSize = totalSize;
        long sizePerFile = totalSize / count;

        for (int i = 0; i < count; i++) {
            long currentFileSize = (i == count - 1) ? remainingSize : sizePerFile;

            byte[] content = createPdfContent(currentFileSize);
            MockMultipartFile file = new MockMultipartFile("file", "test-" + i + ".pdf", "application/pdf", content);
            files.add(file);

            remainingSize -= content.length;
        }

        return files;
    }

    public static List<MultipartFile> generateMockPdfFilesWithImages(int count, int imagesPerPage) {
        List<MultipartFile> files = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, outputStream);
                document.open();

                for (int j = 0; j < imagesPerPage; j++) {
                    int size = 100 + (j * 20);
                    BufferedImage bufferedImage = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics2D = bufferedImage.createGraphics();
                    graphics2D.setColor(java.awt.Color.WHITE);
                    graphics2D.fillRect(0, 0, size, size);
                    graphics2D.setColor(java.awt.Color.BLACK);
                    graphics2D.drawString("Image " + (j + 1), 10, size / 2);
                    graphics2D.dispose();

                    ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
                    write(bufferedImage, "png", imageOutputStream);
                    Image image = Image.getInstance(imageOutputStream.toByteArray());
                    image.scaleToFit(400, 400);
                    document.add(image);
                }

                document.close();

                MultipartFile file = new MockMultipartFile("file", "mock_" + i + ".pdf", "application/pdf", new ByteArrayInputStream(outputStream.toByteArray()));
                files.add(file);
            } catch (Exception e) {
                throw new RuntimeException("Error generating mock PDF", e);
            }
        }

        return files;
    }

    private static byte[] createPdfContent(long targetSize) {
        byte[] content = new byte[(int) targetSize];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }

    public static List<MultipartFile> generateMockGenericFiles(int count) {
        List<MultipartFile> files = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String fileName = "file" + i + ".txt";
            String content = "Dummy content for file " + i;
            MultipartFile file = new MockMultipartFile(fileName, fileName, "text/plain", content.getBytes(StandardCharsets.UTF_8));
            files.add(file);
        }

        return files;
    }

    public static List<MultipartFile> generateMockPdfFilesWithBookmarks(int count, int pagesPerDocument, Map<String, Integer> bookmarkToPage) {
        List<MultipartFile> files = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            try (PDDocument doc = new PDDocument(); ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream()) {

                for (int p = 0; p < pagesPerDocument; p++) {
                    doc.addPage(new PDPage());
                }

                PDDocumentOutline outline = new PDDocumentOutline();
                doc.getDocumentCatalog().setDocumentOutline(outline);

                for (Map.Entry<String, Integer> entry : bookmarkToPage.entrySet()) {
                    String title = entry.getKey();
                    int pageIndex = entry.getValue() - 1;

                    if (pageIndex < 0 || pageIndex >= doc.getNumberOfPages()) {
                        throw new IllegalArgumentException("Bookmark page index out of range: " + pageIndex);
                    }

                    PDOutlineItem bookmark = new PDOutlineItem();
                    bookmark.setTitle(title);

                    PDPageDestination destination = new PDPageFitDestination();
                    destination.setPage(doc.getPage(pageIndex));
                    bookmark.setDestination(destination);

                    outline.addLast(bookmark);
                }

                outline.openNode();
                doc.save(pdfOutput);

                byte[] content = pdfOutput.toByteArray();
                files.add(new MockMultipartFile("file", "test-bookmarked-" + i + ".pdf", "application/pdf", content));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return files;
    }


    private static byte[] createPdfContent(int pagesPerDocument) {
        ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            for (int j = 1; j <= pagesPerDocument; j++) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("Page " + j);
                    contentStream.endText();
                }
            }
            doc.save(pdfOutput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pdfOutput.toByteArray();
    }

}
