package com.gopitch.GoPitch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Bill;
import com.gopitch.GoPitch.domain.Calender;
import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.Pitch;
import com.gopitch.GoPitch.domain.PitchPrice;
import com.gopitch.GoPitch.domain.Placed;
import com.gopitch.GoPitch.domain.User;
import com.gopitch.GoPitch.repository.BillRepository;
import com.gopitch.GoPitch.repository.CalenderRepository;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.repository.PitchPriceRepository;
import com.gopitch.GoPitch.repository.PitchRepository;
import com.gopitch.GoPitch.repository.PlacedRepository;
import com.gopitch.GoPitch.repository.UserRepository;
import com.gopitch.GoPitch.util.SecurityUtil;
import com.gopitch.GoPitch.util.error.BadRequestException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@Service
public class CalenderService {

    private final CalenderRepository calenderRepository;
    private final BillRepository billRepository;
    private final PlacedRepository placedRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final PitchRepository pitchRepository;
    private final PitchPriceRepository pitchPriceRepository;

    public CalenderService(CalenderRepository calenderRepository,
            BillRepository billRepository,
            PlacedRepository placedRepository,
            UserRepository userRepository,
            ClubRepository clubRepository,
            PitchRepository pitchRepository,
            PitchPriceRepository pitchPriceRepository) {
        this.calenderRepository = calenderRepository;
        this.billRepository = billRepository;
        this.placedRepository = placedRepository;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.pitchRepository = pitchRepository;
        this.pitchPriceRepository = pitchPriceRepository;
    }

    public boolean isOrderAlreadyProcessed(Long orderCode) {
        return orderCode != null && billRepository.existsByOrderCode(orderCode);
    }

    @Transactional
    public void createBookingTransaction(List<Map<String, Object>> slots, Long clubId, Double totalAmount,
            Long orderCode) {
        createBookingTransaction(slots, clubId, totalAmount, orderCode, null);
    }

    @Transactional
    public void createBookingTransaction(List<Map<String, Object>> slots, Long clubId, Double totalAmount,
            Long orderCode, String userEmailOverride) {
        if (orderCode != null && billRepository.existsByOrderCode(orderCode)) {
            throw new BadRequestException("Giao dịch đã được xử lý.");
        }

        String email = userEmailOverride;
        if (email == null || email.isBlank()) {
            email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new BadRequestException("Chưa đăng nhập"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CLB"));

        double calculatedTotal = 0;
        for (Map<String, Object> slotMap : slots) {
            Double price = Double.valueOf(slotMap.get("price").toString());
            calculatedTotal += price;
        }

        if (Math.abs(calculatedTotal - totalAmount) > 0.01) {
            throw new BadRequestException("Tổng tiền không khớp với giá các slot đã chọn.");
        }

        for (Map<String, Object> slotMap : slots) {
            Long pitchId = Long.valueOf(slotMap.get("pitchId").toString());
            String dateStr = slotMap.get("date").toString();
            String timeStr = slotMap.get("time").toString();

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime startTime = date.atTime(time);

            Calender calendar = calenderRepository.findByPitchIdAndStartTime(pitchId, startTime);
            if (calendar != null && placedRepository.existsByCalendarIdAndStatusTrue(calendar.getId())) {
                throw new BadRequestException("Slot " + dateStr + " " + timeStr + " đã được đặt.");
            }
        }

        Bill bill = new Bill();
        bill.setUser(user);
        bill.setClub(club);
        bill.setPrice(totalAmount);
        bill.setStatus(true);
        bill.setOrderCode(orderCode);
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

            Calender calendar = calenderRepository.findByPitchIdAndStartTime(pitchId, startTime);

            if (calendar == null) {
                Pitch pitch = pitchRepository.findById(pitchId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sân không tồn tại"));
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

            calendar.setActive(false);
            calenderRepository.save(calendar);
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
