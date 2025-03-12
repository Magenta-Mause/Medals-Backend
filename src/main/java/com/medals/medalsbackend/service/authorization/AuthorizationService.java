package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.config.security.AuthenticationToken;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final Environment environment;

    public boolean isSecurityDisabled() {
        return Arrays.stream(environment.getActiveProfiles()).toList().contains("disableAuth");
    }

    public AuthenticationToken getAuthenticationToken() throws NoAuthenticationFoundException {
        try {
            return (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        } catch (ClassCastException ignored) {
            throw new NoAuthenticationFoundException();
        }
    }

    public void checkUserHasAccess(Long userId) throws NoAuthenticationFoundException, ForbiddenException {
        if (isSecurityDisabled()) {
            return;
        }
        AuthenticationToken authenticationToken = getAuthenticationToken();
        if (!(
            authenticationToken.getSelectedUser().getId().equals(userId)
                || authenticationToken.getSelectedUser().getType().equals(UserType.ADMIN)
                || authenticationToken.getSelectedUser().getType().equals(UserType.TRAINER)
        )) {
            throw new ForbiddenException();
        }
    }

    public UserEntity getSelectedUser() throws NoAuthenticationFoundException {
        return getAuthenticationToken().getSelectedUser();
    }

    public void assertRoleIn(List<UserType> roles) throws NoAuthenticationFoundException, ForbiddenException {
        if (isSecurityDisabled()) {
            return;
        }
        UserEntity selectedUser = getSelectedUser();
        if (roles.stream().noneMatch(role -> role == selectedUser.getType())) {
            throw new ForbiddenException();
        }
    }
}
