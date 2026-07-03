package com.gopitch.GoPitch.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gopitch.GoPitch.config.PayOSProperties;
import com.gopitch.GoPitch.util.error.BadRequestException;
import com.gopitch.GoPitch.util.error.DuplicateResourceException;

@Service
public class PaymentService {

    private final PayOSProperties payOSProperties;
    private final CalenderService calenderService;
    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentService(PayOSProperties payOSProperties, CalenderService calenderService) {
        this.payOSProperties = payOSProperties;
        this.calenderService = calenderService;
    }

    public Map<String, Object> createPaymentLink(int amount) {
        validatePayOSConfig();

        long orderCode = System.currentTimeMillis() / 1000;
        String description = "GO-PITCH " + orderCode;
        String returnUrl = payOSProperties.getReturnUrl();
        String cancelUrl = payOSProperties.getCancelUrl();

        String signatureData = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description
                + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
        String signature = hmacSha256(payOSProperties.getChecksumKey(), signatureData);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderCode", orderCode);
        requestBody.put("amount", amount);
        requestBody.put("description", description);
        requestBody.put("cancelUrl", cancelUrl);
        requestBody.put("returnUrl", returnUrl);
        requestBody.put("signature", signature);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, payOSHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api-merchant.payos.vn/v2/payment-requests", entity, Map.class);

        Object data = response.getBody() != null ? response.getBody().get("data") : null;
        if (data instanceof Map<?, ?> dataMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = new HashMap<>((Map<String, Object>) dataMap);
            result.put("orderCode", orderCode);
            return result;
        }
        throw new BadRequestException("Không thể tạo link thanh toán PayOS.");
    }

    public void confirmPayment(String orderCode, List<Map<String, Object>> slots, Long clubId, Double totalAmount) {
        if (calenderService.isOrderAlreadyProcessed(Long.valueOf(orderCode))) {
            throw new DuplicateResourceException("Giao dịch này đã được xử lý trước đó.");
        }

        String status = fetchPaymentStatus(orderCode);
        if (!"PAID".equals(status)) {
            throw new BadRequestException("Giao dịch chưa được thanh toán.");
        }

        calenderService.createBookingTransaction(slots, clubId, totalAmount, Long.valueOf(orderCode));
    }

    public void handleWebhook(Map<String, Object> payload) {
        if (payload == null || !payload.containsKey("data")) {
            throw new BadRequestException("Webhook payload không hợp lệ.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String orderCode = String.valueOf(data.get("orderCode"));
        String status = String.valueOf(data.get("status"));

        if (!"PAID".equals(status)) {
            return;
        }

        if (calenderService.isOrderAlreadyProcessed(Long.valueOf(orderCode))) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = data.get("metadata") instanceof Map<?, ?> meta
                ? (Map<String, Object>) meta
                : null;

        if (metadata == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> slots = (List<Map<String, Object>>) metadata.get("slots");
        Long clubId = Long.valueOf(metadata.get("clubId").toString());
        Double totalAmount = Double.valueOf(metadata.get("totalAmount").toString());
        String userEmail = metadata.get("userEmail") != null ? metadata.get("userEmail").toString() : null;

        calenderService.createBookingTransaction(slots, clubId, totalAmount, Long.valueOf(orderCode), userEmail);
    }

    public String fetchPaymentStatus(String orderCode) {
        validatePayOSConfig();

        HttpEntity<String> entity = new HttpEntity<>(payOSHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode,
                HttpMethod.GET, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("data") == null) {
            throw new BadRequestException("Không lấy được trạng thái thanh toán.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        return data.get("status").toString();
    }

    private HttpHeaders payOSHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", payOSProperties.getClientId());
        headers.set("x-api-key", payOSProperties.getApiKey());
        return headers;
    }

    private void validatePayOSConfig() {
        if (payOSProperties.getClientId().isBlank()
                || payOSProperties.getApiKey().isBlank()
                || payOSProperties.getChecksumKey().isBlank()) {
            throw new BadRequestException("PayOS chưa được cấu hình. Vui lòng thiết lập biến môi trường PayOS.");
        }
    }

    private String hmacSha256(String key, String data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new BadRequestException("Không thể tạo chữ ký thanh toán.");
        }
    }
}
