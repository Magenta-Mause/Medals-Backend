package com.medals.medalsbackend.dto.authorization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUserDto {
    private String email;
    private String password;
}
