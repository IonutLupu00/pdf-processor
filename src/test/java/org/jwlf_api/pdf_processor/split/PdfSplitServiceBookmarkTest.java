package org.jwlf_api.pdf_processor.split;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jwlf_api.pdf_processor.TestDataGenerator;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jwlf_api.pdf_processor.TestAsserter.PdfAssertDetails;
import static org.jwlf_api.pdf_processor.TestAsserter.assertZipContentWithPdfs;

class PdfSplitServiceBookmarkTest {

    private static final String BOOKMARKS_OPTION_KEY = SplitOption.BOOKMARK_TITLES + "=";
    private PdfSplitServiceBookmark pdfSplitServiceBookmark;

    @BeforeEach
    void setup() {
        pdfSplitServiceBookmark = new PdfSplitServiceBookmark();
    }

    @Test
    void testSplitByBookmark_splitSingleBookmarkSuccess() throws PdfException, IOException {
        String bookmarkName = "BOOKMARK1";
        Map<String, Integer> bookmarkToPage = Map.of(bookmarkName, 3);
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 4, bookmarkToPage).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + bookmarkName);
        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);
        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(2, null, null));
        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_ThrowsIfNoOutline() {
        String bookmarkName = "BOOKMARK1";
        MultipartFile file = TestDataGenerator.generateMockPdfFiles(1).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + bookmarkName);
        PdfException ex = assertThrows(PdfException.class, () -> pdfSplitServiceBookmark.processRequest(request));
        assertTrue(ex.getMessage().contains("No bookmarks found"));
    }

    @Test
    void testSplitByBookmark_ThrowsIfBookmarkNotFound() {
        Map<String, Integer> bookmarkToPage = Map.of("Chapter 1", 1);
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 2, bookmarkToPage).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + "MissingBookmark");
        PdfException ex = assertThrows(PdfException.class, () -> pdfSplitServiceBookmark.processRequest(request));
        assertTrue(ex.getMessage().contains("Bookmark 'MissingBookmark' not found"));
    }

    @Test
    void testSplitByBookmark_splitMultipleBookmarksSuccess() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of(
                "Chapter 1", 1,
                "Chapter 2", 3,
                "Chapter 3", 5
        );
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 6, bookmarkToPage).get(0);
        String bookmarkTitles = String.join(",", bookmarkToPage.keySet());
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + bookmarkTitles);

        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);
        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(2, null, null), "part_2.pdf", new PdfAssertDetails(2, null, null), "part_3.pdf", new PdfAssertDetails(2, null, null));
        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_splitAllBookmarksWhenNoTitlesProvided() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of(
                "Chapter 1", 1,
                "Chapter 2", 3
        );
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 4, bookmarkToPage).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, "");
        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);
        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(2, null, null), "part_2.pdf", new PdfAssertDetails(2, null, null));
        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_lastPageBookmark() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of("LastPage", 4);
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 4, bookmarkToPage).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + "LastPage");

        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(1, null, null));

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_bookmarksOutOfOrder() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of(
                "Chapter 2", 3,
                "Chapter 1", 1
        );
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 4, bookmarkToPage).get(0);
        String bookmarkTitles = "Chapter 2,Chapter 1";
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + bookmarkTitles);

        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(2, null, null), "part_2.pdf", new PdfAssertDetails(2, null, null));

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_singlePagePdfSingleBookmark() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of("OnlyPage", 1);
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 1, bookmarkToPage).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + "OnlyPage");

        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(1, null, null));

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_emptyBookmarksOptionDefaultsToAll() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of(
                "Intro", 1,
                "Body", 2,
                "Conclusion", 4
        );
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 5, bookmarkToPage).get(0);
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY);

        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(1, null, null), "part_2.pdf", new PdfAssertDetails(2, null, null), "part_3.pdf", new PdfAssertDetails(2, null, null));

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }

    @Test
    void testSplitByBookmark_bookmarkOnFirstAndLastPage() throws PdfException, IOException {
        Map<String, Integer> bookmarkToPage = Map.of("First", 1, "Last", 5);
        MultipartFile file = TestDataGenerator.generateMockPdfFilesWithBookmarks(1, 5, bookmarkToPage).get(0);
        String bookmarkTitles = String.join(",", bookmarkToPage.keySet());
        SplitRequest request = new SplitRequest(file, SplitType.BOOKMARK, BOOKMARKS_OPTION_KEY + bookmarkTitles);

        StreamingResponseBody result = pdfSplitServiceBookmark.processRequest(request);

        Map<String, PdfAssertDetails> expectedFilesAndPages = Map.of("part_1.pdf", new PdfAssertDetails(1, null, null), "part_2.pdf", new PdfAssertDetails(4, null, null));

        assertZipContentWithPdfs(result, expectedFilesAndPages);
    }
}
