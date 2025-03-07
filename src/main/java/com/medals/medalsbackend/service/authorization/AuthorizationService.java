package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.config.security.AuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	public void checkUserContainsId(Long userId) throws NoAuthenticationFoundException, UnauthorizedException {
		try {
			AuthenticationToken authToken = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
			if (authToken.getAuthorizedUsers().stream().noneMatch(authorizedEntity -> authorizedEntity.user().getId().equals(userId) && authorizedEntity.authorizationType().equals(AuthenticationToken.AuthorizationType.OWNER))) {
				throw new UnauthorizedException();
			}
		} catch (ClassCastException e) {
			throw new NoAuthenticationFoundException();
		}
	}

	public void checkUserHasBasicAccess(Long userId) {

	}
}
