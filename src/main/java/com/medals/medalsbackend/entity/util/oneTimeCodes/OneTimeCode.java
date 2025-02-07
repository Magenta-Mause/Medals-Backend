package com.medals.medalsbackend.entity.util.oneTimeCodes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneTimeCode {

  @Id
  @GeneratedValue
  public long id;

  @Column(nullable = false, unique = true)
  public String oneTimeCode;

  @Column(nullable = false)
  public String authorizedEmail;

  @Column(nullable = false)
  public OneTimeCodeType type;

  @Column(nullable = false)
  public long expiresAt;
}
