package com.gopitch.GoPitch.controller.client;

import com.gopitch.GoPitch.domain.Bill;
import com.gopitch.GoPitch.domain.response.bill.BillResponseDTO;
import com.gopitch.GoPitch.domain.response.placed.PlacedDTO;
import com.gopitch.GoPitch.repository.BillRepository;
import com.gopitch.GoPitch.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bills")
@CrossOrigin("*") // Cho phép React gọi API
public class BillController {

    @Autowired
    private BillRepository billRepository;

    /**
     * API: Lấy lịch sử đặt sân của người dùng hiện tại
     * GET /api/v1/bills/my-history
     */
    @GetMapping("/my-history")
    public ResponseEntity<?> getMyHistory() {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));

            List<Bill> bills = billRepository.findByUserEmailOrderByCreatedAtDesc(email);

            // Chuyển đổi Entity sang DTO
            List<BillResponseDTO> response = bills.stream().map(bill -> {
                BillResponseDTO dto = new BillResponseDTO();
                dto.setId(bill.getId());
                dto.setPrice(bill.getPrice());
                dto.setCreatedAt(bill.getCreatedAt());
                dto.setClubName(bill.getClub().getName());
                dto.setClubAddress(bill.getClub().getAddress());

                // Map danh sách Placed (slots)
                List<PlacedDTO> slotDTOs = bill.getPlaceds().stream().map(p -> {
                    PlacedDTO pDto = new PlacedDTO();
                    pDto.setId(p.getId());
                    pDto.setPrice(p.getCalendar().getPrice());
                    pDto.setStartTime(p.getCalendar().getStartTime());
                    pDto.setEndTime(p.getCalendar().getEndTime());
                    pDto.setPitchName(p.getCalendar().getPitch().getName());
                    return pDto;
                }).collect(Collectors.toList());

                dto.setSlots(slotDTOs);
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * API: Xem chi tiết một hóa đơn cụ thể (nếu cần)
     * GET /api/v1/bills/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillDetail(@PathVariable Long id) {
        return billRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}