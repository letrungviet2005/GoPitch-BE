package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Calender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalenderRepository extends JpaRepository<Calender, Long> {

    // Tìm slot theo pitch và khoảng thời gian
    List<Calender> findByPitchIdAndStartTimeBetween(Long pitchId, LocalDateTime start, LocalDateTime end);

    // Kiểm tra xem tại pitch này, giờ này đã có slot chưa
    boolean existsByPitchIdAndStartTime(Long pitchId, LocalDateTime startTime);
}