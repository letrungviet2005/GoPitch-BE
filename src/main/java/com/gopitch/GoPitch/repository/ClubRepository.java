package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

    Optional<Club> findByName(String name);

    // Kiểm tra tồn tại theo tên
    boolean existsByName(String name);

    // Kiểm tra tồn tại theo tên nhưng loại trừ id hiện tại
    boolean existsByNameAndIdNot(String name, Long id);

    Page<Club> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Nâng cao: Tìm kiếm theo cả tên HOẶC địa chỉ
    Page<Club> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address,
            Pageable pageable);

}
