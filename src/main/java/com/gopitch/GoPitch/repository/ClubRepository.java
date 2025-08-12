package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Pitch;
import com.gopitch.GoPitch.domain.Calender;
import com.gopitch.GoPitch.domain.Club;

import java.lang.foreign.Linker.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

    Optional<Club> findByName(String name);

}
