package com.gym.system.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log =
            LoggerFactory.getLogger(RestLoggingInterceptor.class);

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);

        log.info(
            "Incoming request: method={} url={} headers={} query={}",
            request.getMethod(),
            request.getRequestURI(),
            getHeaders(request),
            request.getQueryString()
        );
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable Exception ex
    ) {
        long startTime = (long) request.getAttribute(START_TIME);
        long durationMs = System.currentTimeMillis() - startTime;

        if (ex != null) {
            log.error(
                "Request failed: method={} url={} status={} error={} executionTime={}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                ex.getMessage(),
                durationMs,
                ex
            );
        } else {
            log.info(
                "Request completed: method={} url={} status={} executionTime={}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs
            );
        }
    }

    private String getHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .filter(h -> !h.equalsIgnoreCase("authorization"))
                .map(h -> h + "=" + request.getHeader(h))
                .collect(Collectors.joining(", "));
    }
}
