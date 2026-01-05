package com.gopitch.GoPitch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.*;
import com.gopitch.GoPitch.repository.*;
import com.gopitch.GoPitch.util.SecurityUtil;

@Service
public class CalenderService {

    @Autowired
    private CalenderRepository calenderRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private PlacedRepository placedRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private PitchRepository pitchRepository;
    @Autowired
    private PitchPriceRepository pitchPriceRepository;

    @Transactional
    public void createBookingTransaction(List<Map<String, Object>> slots, Long clubId, Double totalAmount) {
        // FIX LỖI 2: Thêm .orElse(null) hoặc đảm bảo kiểu trả về đúng
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));

        // Giả sử findByEmail trong Repo của ông trả về Optional<User>
        User user = userRepository.findByEmail(email).orElse(null);
        // Hoặc
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new RuntimeException("Không tìm thấy CLB"));

        Bill bill = new Bill();
        bill.setUser(user);
        bill.setClub(club);
        bill.setPrice(totalAmount);
        bill.setStatus(true);
        bill.setCreatedAt(LocalDateTime.now().toString());
        bill = billRepository.save(bill);

        for (Map<String, Object> slotMap : slots) {
            Long pitchId = Long.valueOf(slotMap.get("pitchId").toString());
            String dateStr = slotMap.get("date").toString();
            String timeStr = slotMap.get("time").toString();
            Double price = Double.valueOf(slotMap.get("price").toString());

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime startTime = date.atTime(time);

            // FIX LỖI 1: Hàm này đã được thêm vào CalenderRepository
            Calender calendar = calenderRepository.findByPitchIdAndStartTime(pitchId, startTime);

            if (calendar == null) {
                Pitch pitch = pitchRepository.findById(pitchId)
                        .orElseThrow(() -> new RuntimeException("Sân không tồn tại"));
                calendar = new Calender();
                calendar.setPitch(pitch);
                calendar.setPrice(price);
                calendar.setStartTime(startTime);
                calendar.setEndTime(startTime.plusMinutes(60));
                calendar.setActive(true);
                calendar.setCreatedBy(email);
                calendar = calenderRepository.save(calendar);
            }

            Placed placed = new Placed();
            placed.setBill(bill);
            placed.setCalendar(calendar);
            placed.setStatus(true);

            placedRepository.save(placed);
        }
    }

    public void generateForPitch(Long pitchId, LocalDate date) {
        List<PitchPrice> prices = pitchPriceRepository.findByPitchId(pitchId);
        for (PitchPrice priceConfig : prices) {
            generateSlotsFromPrice(priceConfig, date, 60);
        }
    }

    private void generateSlotsFromPrice(PitchPrice priceConfig, LocalDate date, int slotDurationMinutes) {
        LocalTime current = priceConfig.getTimeStart();
        LocalTime end = priceConfig.getTimeEnd();
        while (current.plusMinutes(slotDurationMinutes).isBefore(end)
                || current.plusMinutes(slotDurationMinutes).equals(end)) {
            LocalDateTime startSlot = date.atTime(current);
            if (!calenderRepository.existsByPitchIdAndStartTime(priceConfig.getPitch().getId(), startSlot)) {
                Calender slot = new Calender();
                slot.setPitch(priceConfig.getPitch());
                slot.setPrice(priceConfig.getPrice());
                slot.setStartTime(startSlot);
                slot.setEndTime(date.atTime(current.plusMinutes(slotDurationMinutes)));
                slot.setActive(true);
                slot.setCreatedBy("SYSTEM_AUTO");
                calenderRepository.save(slot);
            }
            current = current.plusMinutes(slotDurationMinutes);
        }
    }
}