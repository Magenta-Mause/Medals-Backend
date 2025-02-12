package com.medals.medalsbackend.dto.authorization;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetPasswordDto {
    public String token;
    @Length(min = 8)
    public String newPassword;
}
