package com.medals.medalsbackend.config.security;

import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

		Optional<UserEntity> selectedUser = userEntityService.findById(Long.parseLong(userId));
		if (selectedUser.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			UserEntity user = selectedUser.get();
			String subject = jwtUtils.validateToken(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
			List<UserEntity> userEntities = userEntityService.getAllByEmail(subject);

			if (!user.getEmail().equals(subject)) {
				filterChain.doFilter(request, response);
				return;
			}

			List<AuthenticationToken.AuthorizedEntity> authenticationTokens = userEntities.stream()
					.map(authEntity -> new AuthenticationToken.AuthorizedEntity(
							authEntity,
							AuthenticationToken.AuthorizationType.OWNER)
					).toList();

			SecurityContextHolder.getContext().setAuthentication(
					new AuthenticationToken(subject, authenticationTokens, user)
			);
		} catch (JwtTokenInvalidException ignored) {
		}
		filterChain.doFilter(request, response);
	}
}
