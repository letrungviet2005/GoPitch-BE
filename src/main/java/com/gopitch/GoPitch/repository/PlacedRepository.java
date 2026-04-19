package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Placed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacedRepository extends JpaRepository<Placed, Long>, JpaSpecificationExecutor<Placed> {

}