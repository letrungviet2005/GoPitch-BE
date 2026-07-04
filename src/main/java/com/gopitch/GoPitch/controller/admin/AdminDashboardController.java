package com.gopitch.GoPitch.controller.admin;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.repository.BillRepository;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.repository.UserRepository;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final BillRepository billRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            ClubRepository clubRepository,
            BillRepository billRepository) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.billRepository = billRepository;
    }

    @GetMapping("/metrics")
    @ApiMessage("Fetch admin dashboard metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        double revenue = billRepository.findAll().stream()
                .mapToDouble(bill -> bill.getPrice())
                .sum();

        return ResponseEntity.ok(Map.of(
                "users", userRepository.count(),
                "clubs", clubRepository.count(),
                "bills", billRepository.count(),
                "revenue", revenue));
    }
}
