package com.gym.system.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogoutSuccessListener
        implements LogoutSuccessHandler {

    private static final Logger log =
            LoggerFactory.getLogger(LogoutSuccessListener.class);

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("Logout triggered!");

        if (authentication != null) {
            log.info("User '{}' logged out successfully",
                    authentication.getName());
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Logged out");
    }
}

