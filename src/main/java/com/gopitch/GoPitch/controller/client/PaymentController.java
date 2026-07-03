package com.gopitch.GoPitch.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.service.PaymentService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment-link")
    @ApiMessage("Create PayOS payment link")
    public ResponseEntity<Map<String, Object>> createPaymentLink(@RequestBody Map<String, Object> data) {
        int amount = Integer.parseInt(data.get("amount").toString());
        return ResponseEntity.ok(paymentService.createPaymentLink(amount));
    }

    @PostMapping("/confirm-payment")
    @ApiMessage("Confirm payment and create booking")
    public ResponseEntity<Map<String, String>> confirmPayment(@RequestBody Map<String, Object> requestData) {
        String orderCode = requestData.get("orderCode").toString();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> slots = (List<Map<String, Object>>) requestData.get("slots");
        Long clubId = Long.valueOf(requestData.get("clubId").toString());
        Double totalAmount = Double.valueOf(requestData.get("totalAmount").toString());

        paymentService.confirmPayment(orderCode, slots, clubId, totalAmount);
        return ResponseEntity.ok(Map.of("message", "Thanh toán thành công. Hóa đơn và lịch đã được lưu!"));
    }

    @PostMapping("/webhook")
    @ApiMessage("PayOS webhook callback")
    public ResponseEntity<Map<String, String>> webhook(@RequestBody Map<String, Object> payload) {
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok(Map.of("message", "OK"));
    }
}
