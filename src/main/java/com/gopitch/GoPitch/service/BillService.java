package com.gopitch.GoPitch.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Bill;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
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

    @Transactional(readOnly = true)
    public ResultPaginationDTO<BillResponseDTO> getAllForAdmin(Pageable pageable) {
        Page<Bill> pageBill = billRepository.findAll(pageable);
        List<BillResponseDTO> bills = pageBill.getContent().stream()
                .map(this::toBillResponseDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageBill.getNumber() + 1,
                pageBill.getSize(),
                pageBill.getTotalPages(),
                pageBill.getTotalElements());

        return new ResultPaginationDTO<>(meta, bills);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAdminRevenueSummary() {
        List<Bill> bills = billRepository.findAll();
        double total = bills.stream().mapToDouble(Bill::getPrice).sum();
        long paidCount = bills.stream().filter(Bill::isStatus).count();

        return Map.of(
                "total", total,
                "count", bills.size(),
                "paidCount", paidCount);
    }

    private BillResponseDTO toBillResponseDTO(Bill bill) {
        BillResponseDTO dto = new BillResponseDTO();
        dto.setId(bill.getId());
        dto.setPrice(bill.getPrice());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setOrderCode(bill.getOrderCode());
        dto.setStatus(bill.isStatus());
        if (bill.getUser() != null) {
            dto.setCustomerName(bill.getUser().getName());
        }
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
