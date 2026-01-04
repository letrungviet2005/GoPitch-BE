package com.gopitch.GoPitch.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gopitch.GoPitch.domain.Pitch;
import com.gopitch.GoPitch.domain.PitchPrice;
import com.gopitch.GoPitch.domain.Club;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, Long> {

    List<Pitch> findByClub(Club club);

    // Sửa lại trong PitchRepository.java
    boolean existsByTimeStartBeforeAndTimeEndAfter(LocalTime startTime, LocalTime endTime);

}
