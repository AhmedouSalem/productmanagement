package com.obs.productmanagement.security;

import com.obs.productmanagement.dto.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Pas de header ou pas "Bearer " → on laisse passer sans auth
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        String email = null;
        Long userId = null;
        String name = null;

        try {
            email = jwtService.extractSubject(token);
            userId = jwtService.extractUserId(token);
            name = jwtService.extractUserName(token);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("""
        {
          "message": "Invalid or malformed JWT token",
          "status": 401,
          "error": "INVALID_JWT"
        }
        """);
            return;
        }


        // Si déjà authentifié, on ne refait pas le travail
        if (email != null && name != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserPrincipal principal = new UserPrincipal(userId, email, name);
            if (jwtService.isTokenValid(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                principal,          // principal (email, userId, name)
                                null,             // pas de credentials
                                Collections.emptyList() // pas de rôles pour l’instant
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
