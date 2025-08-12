package com.gopitch.GoPitch.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gopitch.GoPitch.domain.Pitch;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, Long> {

    // Additional methods specific to Pitch can be defined here if needed

    // Example: Find pitches by club
    // List<Pitch> findByClub(Club club);

    // Example: Check if a pitch is available for a given time range
    // boolean existsByStartTimeBetweenAndEndTimeBetween(LocalDateTime start,
    // LocalDateTime end);

}
