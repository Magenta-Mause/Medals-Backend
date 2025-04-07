package com.medals.medalsbackend.security.jwt;

import com.medals.medalsbackend.entity.users.UserEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class JwtTokenBody {
    private String email;
    private TokenType tokenType;
    private Collection<UserEntity> authorizedUsers;

    public enum TokenType {
        REFRESH_TOKEN,
        IDENTITY_TOKEN,
        REQUEST_TOKEN
    }
}
