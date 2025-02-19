package com.medals.medalsbackend.entity.onetimecode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@ToString
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
