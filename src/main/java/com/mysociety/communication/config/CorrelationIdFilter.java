package com.mysociety.communication.config;

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
class CorrelationIdFilter implements Filter {
    static final String HEADER = "X-Correlation-Id";
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String value = ((HttpServletRequest) request).getHeader(HEADER);
        String correlationId = value != null && value.matches("[A-Za-z0-9-]{1,100}") ? value : UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        ((HttpServletResponse) response).setHeader(HEADER, correlationId);
        try { chain.doFilter(request, response); } finally { MDC.remove("correlationId"); }
    }
}
