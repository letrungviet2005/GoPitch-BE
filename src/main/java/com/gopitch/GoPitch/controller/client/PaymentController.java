package com.gopitch.GoPitch.controller.client;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import com.gopitch.GoPitch.service.CalenderService;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin("*")
public class PaymentController {

    private final String CLIENT_ID = "073f4b0e-084e-4656-813a-4b0b0c75cf30";
    private final String API_KEY = "5c1727a8-732c-462a-8438-97c156b65726";
    private final String CHECKSUM_KEY = "bab5a29f64cec6355a730fc529fceec64b987a2d0124976dc65ed175e34b15d3";

    @Autowired
    private CalenderService calenderService;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/create-payment-link")
    public ResponseEntity<?> createPaymentLink(@RequestBody Map<String, Object> data) {
        try {
            long orderCode = System.currentTimeMillis() / 1000;
            int amount = Integer.parseInt(data.get("amount").toString());
            String description = "GO-PITCH " + orderCode;
            String returnUrl = "http://localhost:5173/payment-success";
            String cancelUrl = "http://localhost:5173/payment-failed";

            String signatureData = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description
                    + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
            String signature = hmacSha256(CHECKSUM_KEY, signatureData);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("orderCode", orderCode);
            requestBody.put("amount", amount);
            requestBody.put("description", description);
            requestBody.put("cancelUrl", cancelUrl);
            requestBody.put("returnUrl", returnUrl);
            requestBody.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", CLIENT_ID);
            headers.set("x-api-key", API_KEY);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate
                    .postForEntity("https://api-merchant.payos.vn/v2/payment-requests", entity, Map.class);

            return ResponseEntity.ok(response.getBody().get("data"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> requestData) {
        try {
            String orderCode = requestData.get("orderCode").toString();
            List<Map<String, Object>> slots = (List<Map<String, Object>>) requestData.get("slots");
            Long clubId = Long.valueOf(requestData.get("clubId").toString());
            Double totalAmount = Double.valueOf(requestData.get("totalAmount").toString());

            // 1. Xác thực trạng thái thanh toán từ PayOS
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", CLIENT_ID);
            headers.set("x-api-key", API_KEY);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode,
                    HttpMethod.GET, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            String status = ((Map) responseBody.get("data")).get("status").toString();

            // 2. Nếu đã trả tiền (PAID), thực hiện transaction lưu 3 bảng
            if ("PAID".equals(status)) {
                calenderService.createBookingTransaction(slots, clubId, totalAmount);
                return ResponseEntity.ok(Map.of("message", "Thanh toán thành công. Hóa đơn và lịch đã được lưu!"));
            }

            return ResponseEntity.status(400).body("Giao dịch chưa được thanh toán.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private String hmacSha256(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}