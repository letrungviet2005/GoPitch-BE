package com.gopitch.GoPitch.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.domain.response.bill.BillResponseDTO;
import com.gopitch.GoPitch.service.BillService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/my-history")
    @ApiMessage("Get booking history for current user")
    public ResponseEntity<List<BillResponseDTO>> getMyHistory() {
        return ResponseEntity.ok(billService.getMyHistory());
    }

    @GetMapping("/{id}")
    @ApiMessage("Get bill detail for current user")
    public ResponseEntity<BillResponseDTO> getBillDetail(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillDetail(id));
    }
}
