package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.users.LoginEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginEntryRepository extends JpaRepository<LoginEntry, String> {
}
