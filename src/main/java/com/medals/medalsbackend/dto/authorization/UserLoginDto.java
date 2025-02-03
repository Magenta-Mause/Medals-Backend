package com.medals.medalsbackend.dto.authorization;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
