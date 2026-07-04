package com.gopitch.GoPitch.controller.admin;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.bill.BillResponseDTO;
import com.gopitch.GoPitch.service.BillService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin/revenue")
public class RevenueController {

    private final BillService billService;

    public RevenueController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/transactions")
    @ApiMessage("Fetch payment transactions for admin")
    public ResponseEntity<ResultPaginationDTO<BillResponseDTO>> getTransactions(Pageable pageable) {
        return ResponseEntity.ok(billService.getAllForAdmin(pageable));
    }

    @GetMapping("/summary")
    @ApiMessage("Fetch revenue summary for admin")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(billService.getAdminRevenueSummary());
    }
}
