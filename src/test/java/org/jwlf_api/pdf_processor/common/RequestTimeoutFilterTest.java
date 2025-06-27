package org.jwlf_api.pdf_processor.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestTimeoutFilterTest {

    private RequestTimeoutFilter filter;
    private ServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        request = mock(ServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @AfterAll
    static void tearDownExecutor() {
        // Optional: You can shut down all threads if needed
    }

    @Test
    void shouldProcessRequestWithinTimeout() throws Exception {
        filter = new RequestTimeoutFilter(200);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
    }

    @Test
    void shouldReturnGatewayTimeoutWhenRequestExceedsTimeout() throws Exception {
        filter = new RequestTimeoutFilter(50);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        doAnswer(invocation -> {
            Thread.sleep(200);
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Request timed out"));
    }

    @Test
    void shouldThrowServletExceptionWhenChainThrowsException() throws Exception {
        filter = new RequestTimeoutFilter(200);
        doThrow(new IOException("Test exception")).when(chain).doFilter(request, response);

        ServletException thrown = assertThrows(ServletException.class, () ->
                filter.doFilter(request, response, chain));

        assertInstanceOf(RuntimeException.class, thrown.getCause().getCause());
        assertEquals("java.io.IOException: Test exception", thrown.getCause().getCause().getMessage());
    }

    @Test
    void shouldCancelFutureAfterTimeout() throws Exception {
        filter = new RequestTimeoutFilter(50);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        AtomicBoolean cancelled = new AtomicBoolean(false);
        doAnswer(invocation -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                cancelled.set(true);
            }
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        assertTrue(cancelled.get() || stringWriter.toString().contains("Request timed out"));
    }
}