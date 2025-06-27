package org.jwlf_api.pdf_processor.split;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.jwlf_api.pdf_processor.common.PdfException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.jwlf_api.pdf_processor.common.Util.createZipFromFilePaths;
import static org.jwlf_api.pdf_processor.split.PdfSplitter.PageRange;
import static org.jwlf_api.pdf_processor.split.PdfSplitter.splitByPageRanges;


@Slf4j
@Service
public class PdfSplitServiceBookmark extends PdfSplitService {

    private static final String NO_BOOKMARKS_FOUND = "No bookmarks found in the PDF.";
    private static final String BOOKMARK_NOT_FOUND = "Bookmark '%s' not found.";

    @Override
    public StreamingResponseBody splitPdf(SplitRequest request) throws PdfException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(request.getFile().getInputStream()); PDDocument document = Loader.loadPDF(rar)) {
            Map<SplitOption, String> options = parseOptions(request.getOptions());
            List<String> bookmarks = CollectionUtils.isEmpty(options) ? null : parseBookmarks(options.get(SplitOption.BOOKMARK_TITLES));
            List<Path> documents = splitDocuments(document, bookmarks);
            return createZipFromFilePaths(documents);
        } catch (IOException e) {
            throw new PdfSplitException("Failed to load PDF document.", e);
        }
    }

    private List<Path> splitDocuments(PDDocument document, List<String> requestedBookmarks) throws IOException, PdfSplitException {
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        if (outline == null || outline.getFirstChild() == null) {
            throw new PdfSplitException(NO_BOOKMARKS_FOUND);
        }

        List<Integer> bookmarkPageIndexes = (requestedBookmarks == null || requestedBookmarks.isEmpty())
                ? collectAllBookmarkPages(document, outline)
                : resolveRequestedBookmarks(document, outline, requestedBookmarks);

        if (bookmarkPageIndexes.isEmpty()) {
            throw new PdfSplitException(NO_BOOKMARKS_FOUND);
        }

        List<PageRange> pageRanges = getPageRanges(document, bookmarkPageIndexes);
        return splitByPageRanges(document, pageRanges);
    }

    private static List<PageRange> getPageRanges(PDDocument document, List<Integer> bookmarkPageIndexes) {
        if (!CollectionUtils.isEmpty(bookmarkPageIndexes) && bookmarkPageIndexes.size() == 1) {
            PageRange pageRange = new PageRange(bookmarkPageIndexes.getFirst(), document.getNumberOfPages());
            return List.of(pageRange);
        }

        List<PageRange> pageRanges = new ArrayList<>(bookmarkPageIndexes.size() + 1);

        for (int i = 1; i < bookmarkPageIndexes.size(); i++) {
            if (i == bookmarkPageIndexes.size() - 1 && bookmarkPageIndexes.get(i) <= document.getNumberOfPages()) {
                PageRange pageRange = new PageRange(bookmarkPageIndexes.get(i), document.getNumberOfPages());
                pageRanges.add(pageRange);
            }
            PageRange pageRange = new PageRange(bookmarkPageIndexes.get(i - 1), bookmarkPageIndexes.get(i) - 1);
            pageRanges.add(pageRange);
        }
        return pageRanges;
    }

    private List<Integer> resolveRequestedBookmarks(PDDocument document, PDOutlineNode outline, List<String> titles) throws IOException, PdfSplitException {
        List<Integer> pageIndexes = new ArrayList<>(titles.size());
        for (String title : titles) {
            PDOutlineItem item = findBookmarkByTitle(outline, title);
            if (item == null) {
                throw new PdfSplitException(String.format(BOOKMARK_NOT_FOUND, title));
            }
            int pageIndex = resolvePageIndex(document, item);
            if (pageIndex <= 0) {
                throw new PdfSplitException(String.format("Could not resolve page for bookmark '%s'", title));
            }
            pageIndexes.add(pageIndex);
        }
        return pageIndexes.stream().sorted().toList();
    }

    private int resolvePageIndex(PDDocument document, PDOutlineItem item) throws IOException {
        PDPage page = item.findDestinationPage(document);
        if (page == null) {
            return -1;
        }
        int pageIndex = document.getPages().indexOf(page);
        if (pageIndex >= 0) {
            return pageIndex + 1;
        }
        int i = 0;
        for (PDPage p : document.getPages()) {
            if (p == page || p.getCOSObject() == page.getCOSObject() || p.equals(page)) {
                return i + 1;
            }
            i++;
        }
        return -1;
    }

    private List<Integer> collectAllBookmarkPages(PDDocument document, PDOutlineNode parent) {
        return outlineItems(parent)
                .flatMap(item -> {
                    int index = -1;
                    try {
                        index = resolvePageIndex(document, item);
                    } catch (IOException ignored) {
                        //Ignored
                    }
                    Stream<Integer> current = index > 0 ? Stream.of(index) : Stream.empty();
                    Stream<Integer> children = collectAllBookmarkPages(document, item).stream();
                    return Stream.concat(current, children);
                })
                .sorted()
                .toList();
    }

    private Stream<PDOutlineItem> outlineItems(PDOutlineNode parent) {
        Iterable<PDOutlineItem> it = () -> new Iterator<>() {
            private PDOutlineItem current = parent.getFirstChild();

            public boolean hasNext() {
                return current != null;
            }

            public PDOutlineItem next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                PDOutlineItem item = current;
                current = current.getNextSibling();
                return item;
            }
        };
        return StreamSupport.stream(it.spliterator(), false);
    }


    private PDOutlineItem findBookmarkByTitle(PDOutlineNode parent, String title) {
        return outlineItems(parent)
                .map(item -> title.equalsIgnoreCase(item.getTitle().trim()) ? item : findBookmarkByTitle(item, title))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private List<String> parseBookmarks(String bookmarksStr) {
        if (bookmarksStr == null || bookmarksStr.isBlank()) return Collections.emptyList();
        return Stream.of(bookmarksStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}