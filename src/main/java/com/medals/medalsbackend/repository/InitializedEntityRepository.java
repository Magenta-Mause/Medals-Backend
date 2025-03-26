package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.initializedentity.InitializedEntity;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InitializedEntityRepository extends JpaRepository<InitializedEntity, InitializedEntityType> {
}
