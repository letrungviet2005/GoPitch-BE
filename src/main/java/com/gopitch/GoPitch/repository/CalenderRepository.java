package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Calender;
import com.gopitch.GoPitch.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CalenderRepository extends JpaRepository<Calender, Long> {

}
