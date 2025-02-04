package com.medals.medalsbackend.dto.authorization;

import lombok.Data;

@Data
public class SetPasswordDto {
  private String oneTimeCode;
  private String password;
}
