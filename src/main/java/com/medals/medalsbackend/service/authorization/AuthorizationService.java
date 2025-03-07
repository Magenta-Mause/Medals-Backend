package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.config.security.AuthenticationToken;
import com.medals.medalsbackend.entity.users.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	public AuthenticationToken getAuthenticationToken() throws NoAuthenticationFoundException {
		try {
			return (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		} catch (ClassCastException ignored) {
			throw new NoAuthenticationFoundException();
		}
	}

	public void checkUserHasFullAccess(Long userId) throws NoAuthenticationFoundException, UnauthorizedException {
		AuthenticationToken authenticationToken = getAuthenticationToken();
		if (authenticationToken.getAuthorizedUsers().stream().noneMatch(authorizedEntity -> authorizedEntity.user().getId().equals(userId) && authorizedEntity.authorizationType().equals(AuthenticationToken.AuthorizationType.OWNER))) {
			throw new UnauthorizedException();
		}
	}

	public void checkUserHasBasicAccess(Long userId) throws NoAuthenticationFoundException {
		AuthenticationToken authenticationToken = getAuthenticationToken();
		if (authenticationToken.getAuthorizedUsers().stream().noneMatch(authorizedEntity -> authorizedEntity.user().getId().equals(userId))) {
			throw new NoAuthenticationFoundException();
		}
	}

	public UserEntity getSelectedUser() throws NoAuthenticationFoundException {
		return getAuthenticationToken().getSelectedUser();
	}
}
