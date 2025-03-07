package com.medals.medalsbackend.config.security;

import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNullApi;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtils jwtUtils;
  private final LoginEntryService loginEntryService;
  private final UserEntityService userEntityService;

  private String resolveToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    return request.getParameter("authToken");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = resolveToken(request);
    if (token != null) {
      try {
        String subject = jwtUtils.validateToken(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
        List<UserEntity> userEntities = userEntityService.getAllByEmail(subject);
        userEntities.forEach(System.out::println);
        List<AuthenticationToken.AuthorizedEntity> authenticationTokens = userEntities.stream()
          .map(authEntity -> new AuthenticationToken.AuthorizedEntity(
            authEntity,
            AuthenticationToken.AuthorizationType.OWNER)
          ).toList();
        SecurityContextHolder.getContext().setAuthentication(
          new AuthenticationToken(subject, authenticationTokens)
        );
        authenticationTokens.forEach(System.out::println);
      } catch (JwtTokenInvalidException ignored) {}
    }
    filterChain.doFilter(request, response);
  }
}
