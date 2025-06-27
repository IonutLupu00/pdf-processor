package org.jwlf_api.pdf_processor.common;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RequestTimeoutFilter implements Filter {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final long timeoutMillis;

    public RequestTimeoutFilter(@Value("${request.timeout:30000}") long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Future<?> future = executor.submit(() -> {
            try {
                chain.doFilter(request, response);
            } catch (IOException | ServletException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            HttpServletResponse http = (HttpServletResponse) response;
            http.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
            http.getWriter().write("Request timed out");
            future.cancel(true);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
