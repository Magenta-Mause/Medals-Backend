package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.medals.InitializedEntity;
import com.medals.medalsbackend.entity.medals.InitializedEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InitializedEntityRepository extends JpaRepository<InitializedEntity, InitializedEntityType> {
}
