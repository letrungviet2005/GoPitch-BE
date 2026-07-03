package com.gopitch.GoPitch.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gopitch.GoPitch.domain.PitchPrice;
import java.util.List;

@Repository
public interface PitchPriceRepository extends JpaRepository<PitchPrice, Long> {
    boolean existsByPitchIdAndPrice(long pitchId, double price);

    boolean existsByPitchIdAndPriceAndIdNot(long pitchId, double price, Long id);

    PitchPrice findByPitchIdAndPrice(long pitchId, double price);

    PitchPrice findByPitchIdAndId(long pitchId, Long id);

    List<PitchPrice> findByPitchId(Long pitchId);

}
