package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface PerformanceRecordingRepository extends JpaRepository<PerformanceRecording, Long> {
    @Query("SELECT r FROM performance_recording r WHERE r.athlete.id=?1")
    Collection<PerformanceRecording> getAllByAthleteId(Long id);
}
