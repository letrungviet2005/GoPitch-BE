package com.gopitch.GoPitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gopitch.GoPitch.domain.Bill;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserEmailOrderByCreatedAtDesc(String email);
}