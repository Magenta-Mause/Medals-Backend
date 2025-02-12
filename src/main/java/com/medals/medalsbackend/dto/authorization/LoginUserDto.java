package com.medals.medalsbackend.dto.authorization;

import lombok.Data;

@Data
public class LoginUserDto {
    private String email;
    private String password;
}
