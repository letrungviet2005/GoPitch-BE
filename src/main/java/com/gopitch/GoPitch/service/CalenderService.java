package com.gopitch.GoPitch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gopitch.GoPitch.domain.Calender;
import com.gopitch.GoPitch.domain.PitchPrice;
import com.gopitch.GoPitch.repository.CalenderRepository;
import com.gopitch.GoPitch.repository.PitchPriceRepository;

@Service
public class CalenderService {

    @Autowired
    private CalenderRepository calenderRepository;

    @Autowired
    private PitchPriceRepository pitchPriceRepository;

    /**
     * Quy trình tạo lịch cho 1 sân cụ thể dựa trên bảng giá
     */
    public void generateForPitch(Long pitchId, LocalDate date) {
        // 1. Lấy tất cả các khung giá của sân đó
        List<PitchPrice> prices = pitchPriceRepository.findByPitchId(pitchId);

        for (PitchPrice priceConfig : prices) {
            // Mặc định tạo slot 60 phút (Bạn có thể đổi thành 30 tùy ý)
            generateSlotsFromPrice(priceConfig, date, 60);
        }
    }

    /**
     * Chia nhỏ PitchPrice thành các slot Calender
     */
    public void generateSlotsFromPrice(PitchPrice priceConfig, LocalDate date, int slotDurationMinutes) {
        LocalTime current = priceConfig.getTimeStart();
        LocalTime end = priceConfig.getTimeEnd();

        while (current.plusMinutes(slotDurationMinutes).isBefore(end) ||
                current.plusMinutes(slotDurationMinutes).equals(end)) {

            LocalDateTime startSlot = date.atTime(current);

            // KIỂM TRA: Nếu đã tồn tại slot cho sân này vào giờ này thì không tạo nữa
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