package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

    Optional<Club> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    // THÊM LẠI HÀM NÀY (Hàm mà Service đang báo thiếu)
    Page<Club> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Hàm tìm kiếm mở rộng cả tên và địa chỉ
    Page<Club> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(
            String name, String address, Pageable pageable);
}