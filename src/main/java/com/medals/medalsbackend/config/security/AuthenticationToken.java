package com.medals.medalsbackend.config.security;

import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
	List<AuthorizedEntity> authorizedUsers;
	UserEntity selectedUser;

	public AuthenticationToken(String email, List<AuthorizedEntity> authorizedUsers, UserEntity selectedUser) {
		super(email, null, Stream.concat(authorizedUsers.stream()
						.map(user ->
								"ROLE_" +
										(user.authorizationType == AuthorizationType.OWNER
												? "USER_"
												: user.authorizationType == AuthorizationType.ADMINISTRATOR
												? "ADMINISTRATES_"
												: "INVALID_"
										) + user.user.getId())
				, (authorizedUsers.stream().map(
						user ->
								(user.user.getType() == UserType.ATHLETE
										? "ATHLETE"
										: user.user.getType() == UserType.ADMIN
										? "ADMIN"
										: user.user.getType() == UserType.TRAINER
										? "TRAINER"
										: "")
				))).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
		);
		this.selectedUser = selectedUser;
		this.authorizedUsers = authorizedUsers;
	}

	public enum AuthorizationType {
		OWNER,
		ADMINISTRATOR
	}

	public record AuthorizedEntity(UserEntity user, AuthorizationType authorizationType) {
	}
}
