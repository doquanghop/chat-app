package com.github.doquanghop.chat_app.application.fliter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestIdFilter extends OncePerRequestFilter implements Ordered {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID_KEY = "requestId";

    private static final int ORDER = 50;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest servletRequest,
            @NonNull HttpServletResponse servletResponse,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException{
        String requestId = servletRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        servletResponse.setHeader(REQUEST_ID_HEADER, requestId);
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.remove(MDC_REQUEST_ID_KEY);
    }

    public static String getRequestId() {
        return MDC.get(MDC_REQUEST_ID_KEY);
    }
}
