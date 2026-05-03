package com.gym.system.filter;

import java.io.IOException;
import java.util.Optional;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    public static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String transactionId = Optional.ofNullable(
                request.getHeader(TRANSACTION_ID)
        ).orElse(UUID.randomUUID().toString());

        MDC.put(TRANSACTION_ID, transactionId);

        response.setHeader(TRANSACTION_ID, transactionId);

        try {
            logger.info("-----Transaction started-----");
            filterChain.doFilter(request, response);
        } finally {
            logger.info("-----Transaction completed-----");
            MDC.clear();
        }
    }
}
