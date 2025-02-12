package com.medals.medalsbackend.dto.authorization;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class SetPasswordDto {
  private String oneTimeCode;
  @Length(min=8)
  private String password;
}
