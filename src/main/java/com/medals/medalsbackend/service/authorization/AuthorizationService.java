package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.config.security.AuthenticationToken;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.service.user.AthleteService;
import com.medals.medalsbackend.service.user.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final Environment environment;
    private final AthleteService athleteService;
    private final TrainerService trainerService;

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


    public UserEntity getSelectedUser() throws NoAuthenticationFoundException {
        return getAuthenticationToken().getSelectedUser();
    }

    public void assertUserHasOwnerAccess(Long userId) throws NoAuthenticationFoundException, ForbiddenException {
        if (isSecurityDisabled()) {
            return;
        }
        UserEntity user = getSelectedUser();
        if (!user.getId().equals(userId) && !user.getType().equals(UserType.ADMIN)) {
            throw new ForbiddenException();
        }
    }

    @SneakyThrows
    public void assertUserHasAccess(Long userId) throws NoAuthenticationFoundException, ForbiddenException {
        if (isSecurityDisabled()) {
            return;
        }
        UserEntity user = getSelectedUser();
        if (
            user.getId().equals(userId)
        ) {
            return;
        }
        if (user.getType().equals(UserType.ADMIN)) {
            return;
        }
        if (user.getType().equals(UserType.TRAINER)) {
            if (trainerService.getTrainer(user.getId()).getAssignedAthletes().stream().anyMatch(athlete -> athlete.getId().equals(userId))) {
                return;
            }
        }
        throw new ForbiddenException();
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
