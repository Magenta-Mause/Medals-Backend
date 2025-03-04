package com.medals.medalsbackend.dto.authorization;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
@Data
@Builder
public class InviteAthleteDto {
    public String token;
    public String email;
    @Length(min = 8)
    public String password;
}
