package com.gopitch.GoPitch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Bill;
import com.gopitch.GoPitch.domain.response.bill.BillResponseDTO;
import com.gopitch.GoPitch.domain.response.placed.PlacedDTO;
import com.gopitch.GoPitch.repository.BillRepository;
import com.gopitch.GoPitch.util.SecurityUtil;
import com.gopitch.GoPitch.util.error.PermissionException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Transactional(readOnly = true)
    public List<BillResponseDTO> getMyHistory() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new PermissionException("Chưa đăng nhập"));

        return billRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(this::toBillResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BillResponseDTO getBillDetail(long id) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new PermissionException("Chưa đăng nhập"));

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn id: " + id));

        if (bill.getUser() == null || !email.equals(bill.getUser().getEmail())) {
            throw new PermissionException("Bạn không có quyền xem hóa đơn này.");
        }

        return toBillResponseDTO(bill);
    }

    private BillResponseDTO toBillResponseDTO(Bill bill) {
        BillResponseDTO dto = new BillResponseDTO();
        dto.setId(bill.getId());
        dto.setPrice(bill.getPrice());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setClubName(bill.getClub().getName());
        dto.setClubAddress(bill.getClub().getAddress());

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
    }
}
