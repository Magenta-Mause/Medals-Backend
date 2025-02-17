package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.onetimecode.OneTimeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface OneTimeCodeRepository extends JpaRepository<OneTimeCode, Long> {
  OneTimeCode findByOneTimeCode(String code);
  @Query("SELECT a FROM OneTimeCode as a")
  Collection<OneTimeCode> getAll();
  void deleteAllByOneTimeCode(String oneTimeCode);
}
