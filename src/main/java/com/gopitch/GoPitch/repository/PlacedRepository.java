package com.gopitch.GoPitch.repository;

import com.gopitch.GoPitch.domain.Placed; // Nhớ import đúng domain Placed
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
// Sửa Club thành Placed ở đây
public interface PlacedRepository extends JpaRepository<Placed, Long>, JpaSpecificationExecutor<Placed> {

    // Xóa các hàm findByName, existsByName đi vì bảng Placed thường không có trường
    // "Name"
    // Nếu ông muốn tìm kiếm gì ở bảng Placed thì thêm sau
}