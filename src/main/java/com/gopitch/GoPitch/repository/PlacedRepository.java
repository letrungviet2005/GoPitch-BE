package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlacedRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

}
