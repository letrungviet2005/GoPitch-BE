package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.ExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExtraServiceRepository extends JpaRepository<ExtraService, Long> {
    List<ExtraService> findByClubId(long clubId);
}
