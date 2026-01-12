package com.gopitch.GoPitch.controller.client;

import com.gopitch.GoPitch.service.AIService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chatWithAI(@RequestBody ChatRequest request) {
        // Chỉ gọi Service xử lý, Controller không cần quan tâm logic bên trong
        String reply = aiService.getAIResponse(request.getMessage(), request.getLat(), request.getLng());
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}

@Data
class ChatRequest {
    private String message;
    private double lat;
    private double lng;
}