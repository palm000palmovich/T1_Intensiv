package org.example.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            logger.info("Полученный токен: " + token);
            if (jwtUtil.validateServiceToken(token)) {
                String subject = jwtUtil.extractSubject(token);
                if ("service-account".equals(subject)) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            subject, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.info("Токен успешно проверен.");
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token subject");
                    return;
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                logger.info("Невалидный токен.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
