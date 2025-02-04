package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OneTimeCodeRepository extends JpaRepository<OneTimeCode, Long> {
  OneTimeCode findByOneTimeCode(String code);

  void deleteAllByOneTimeCode(String oneTimeCode);
}
