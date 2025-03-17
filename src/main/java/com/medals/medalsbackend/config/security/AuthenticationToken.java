package com.medals.medalsbackend.config.security;

import com.medals.medalsbackend.entity.users.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
@Setter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    UserEntity selectedUser;

    public AuthenticationToken(String email, UserEntity selectedUser) {
        super(email, null, List.of(new SimpleGrantedAuthority("ROLE_" + selectedUser.getType().name())));
        this.selectedUser = selectedUser;
    }

}
