package com.payetonkawa.order.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        Instant start = Instant.now();

        chain.doFilter(request, response);

        Instant end = Instant.now();
        long durationMs = Duration.between(start, end).toMillis();

        String remoteAddr = req.getRemoteAddr();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        int status = res.getStatus();

        log.info("http_request: method={} uri={} status={} durationMs={} clientIp={}",
                method, uri, status, durationMs, remoteAddr);
    }
}
