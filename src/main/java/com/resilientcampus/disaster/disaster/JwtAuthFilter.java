package com.resilientcampus.disaster.disaster;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/health",
        "/api/trigger-sos",
        "/api/upload",
        "/api/auth"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        if ("OPTIONS".equals(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        if (!isPublic) {
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                httpResponse.setStatus(401);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"success\":false,\"message\":\"Unauthorized - No token provided\"}");
                return;
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                httpResponse.setStatus(401);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"success\":false,\"message\":\"Unauthorized - Invalid or expired token\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
