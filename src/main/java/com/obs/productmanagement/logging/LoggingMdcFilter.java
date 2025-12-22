package com.obs.productmanagement.logging;

import com.obs.productmanagement.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String email = SecurityUtils.getCurrentUserEmail();

            if (userId != null) MDC.put("userId", String.valueOf(userId));
            if (email != null) MDC.put("userEmail", email);

            MDC.put("httpMethod", request.getMethod());
            MDC.put("httpPath", request.getRequestURI());

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
