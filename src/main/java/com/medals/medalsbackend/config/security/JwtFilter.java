package com.medals.medalsbackend.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return request.getParameter("authToken");
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader("X-Selected-User");
        if (userId != null) {
            return userId;
        }
        return request.getParameter("selectedUser");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        String userId = getUserIdFromRequest(request);

        if (userId == null || token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Map<String, Object> tokenBody;
        try {
            tokenBody = jwtUtils.getJwtTokenClaims(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
        } catch (JwtTokenInvalidException e) {
            filterChain.doFilter(request, response);
            return;
        }
        List<UserEntity> users = objectMapper.convertValue(tokenBody.get("users"), new TypeReference<>() {
        });

        Optional<UserEntity> selectedUser = users.stream().filter(user -> user.getId().equals(Long.parseLong(userId))).findFirst();
        if (selectedUser.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserEntity user = selectedUser.get();
            String subject = jwtUtils.getJwtTokenUser(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);

            if (!user.getEmail().equals(subject)) {
                filterChain.doFilter(request, response);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(
                new AuthenticationToken(subject, user)
            );
        } catch (JwtTokenInvalidException ignored) {
        }
        filterChain.doFilter(request, response);
    }
}
