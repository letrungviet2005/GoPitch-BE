package com.gopitch.GoPitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gopitch.GoPitch.domain.Bill;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    // Tìm danh sách bill theo email user và sắp xếp mới nhất lên đầu
    List<Bill> findByUserEmailOrderByCreatedAtDesc(String email);
}