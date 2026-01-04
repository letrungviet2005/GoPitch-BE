package com.gopitch.GoPitch.controller.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gopitch.GoPitch.domain.Calender;
import com.gopitch.GoPitch.repository.CalenderRepository;
import com.gopitch.GoPitch.service.CalenderService;

@RestController
@RequestMapping("/api/v1/calendars")
@CrossOrigin("*")
public class CalenderController {

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private CalenderRepository calenderRepository;

    /**
     * API Tạo lịch tự động: POST
     * /api/v1/calendars/generate?pitchId=1&date=2026-01-02
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateSlots(
            @RequestParam Long pitchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        calenderService.generateForPitch(pitchId, date);
        return ResponseEntity.ok("Success: Generated slots for " + date);
    }

    /**
     * API Lấy lịch hiển thị FE: GET /api/v1/calendars?pitchId=1&date=2026-01-02
     */
    @GetMapping
    public ResponseEntity<List<Calender>> getCalendarsByDate(
            @RequestParam Long pitchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Calender> list = calenderRepository.findByPitchIdAndStartTimeBetween(pitchId, startOfDay, endOfDay);
        return ResponseEntity.ok(list);
    }
}