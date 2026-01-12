package com.gopitch.GoPitch.service;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {

    @Autowired
    private ClubRepository clubRepository;

    private final String GEMINI_API_KEY = "AIzaSyBqSzn9ex_A3bPlFfpMabInU0JI0fGo-LU";
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";

    public String getAIResponse(String userMessage, double userLat, double userLng) {
        try {
            // 1. Lấy dữ liệu sân bóng xung quanh (Bán kính 15km cho rộng rãi)
            List<Club> nearestClubs = clubRepository.findNearestClubs(userLat, userLng, 15.0);

            // 2. Xây dựng ngữ cảnh siêu thông minh
            StringBuilder context = new StringBuilder();
            context.append("Bối cảnh: Bạn là GoPitch AI - Chuyên gia tư vấn thể thao và đặt sân bóng tại Đà Nẵng.\n");
            context.append(String.format("Tọa độ khách hiện tại: [%f, %f].\n", userLat, userLng));
            context.append(
                    "Quy tắc: Luôn thân thiện, chuyên nghiệp. Nếu khách hỏi tìm sân, hãy dựa vào dữ liệu bên dưới.\n\n");

            context.append("DANH SÁCH SÂN BÓNG THỰC TẾ (Dùng ID để tạo link):\n");
            if (nearestClubs.isEmpty()) {
                context.append("- Hiện không có sân nào trong hệ thống gần vị trí này.\n");
            } else {
                for (Club club : nearestClubs) {
                    context.append(String.format("📍 %s (ID: %d)\n", club.getName(), club.getId()));
                    context.append(String.format("   - Địa chỉ: %s\n", club.getAddress()));

                    // Thêm thông tin môn thể thao nếu có
                    if (club.getPitches() != null) {
                        String sports = club.getPitches().stream()
                                .map(p -> p.getName())
                                .distinct()
                                .collect(Collectors.joining(", "));
                        context.append(String.format("   - Loại sân/Môn: %s\n", sports));
                    }

                    if (club.getPitchPrices() != null && !club.getPitchPrices().isEmpty()) {
                        context.append("   - Giá tham khảo: ");
                        var minPrice = club.getPitchPrices().stream().mapToDouble(p -> p.getPrice()).min().orElse(0);
                        context.append(String.format("Từ %,.0f VNĐ/giờ\n", minPrice));
                    }
                    context.append("\n");
                }
            }

            // 3. Prompt hướng dẫn hành vi (Cực kỳ quan trọng để AI khôn hơn)
            String finalPrompt = context.toString() +
                    "\n---\n" +
                    "TIN NHẮN CỦA KHÁCH: \"" + userMessage + "\"\n\n" +
                    "NHIỆM VỤ CỦA BẠN:\n" +
                    "1. Nếu là câu chào (ví dụ: chào bạn, hello...): Hãy chào lại thật nhiệt tình và giới thiệu bạn có thể giúp tìm sân.\n"
                    +
                    "2. Nếu hỏi tìm sân: Hãy liệt kê khoảng 2-3 sân phù hợp nhất, in đậm tên sân. BẮT BUỘC có link: [👉 Đặt sân ngay](/detailpitch/ID).\n"
                    +
                    "3. Nếu khách hỏi về môn thể thao cụ thể (ví dụ: cầu lông): Chỉ lọc ra các sân có từ khóa đó.\n" +
                    "4. Luôn trình bày đẹp mắt bằng Markdown, xuống dòng rõ ràng.\n" +
                    "5. Tuyệt đối không tự bịa ra ID sân nếu không có trong danh sách.";

            // 4. Gửi Request
            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", finalPrompt)))));

            URI uri = new URI(GEMINI_URL + GEMINI_API_KEY);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> response = restTemplate.postForEntity(uri, new HttpEntity<>(body, headers), Map.class);

            // 5. Bóc tách dữ liệu an toàn (Chống NullPointerException)
            if (response.getBody() == null || !response.getBody().containsKey("candidates")) {
                return "GoPitch AI đang suy nghĩ quá tải, đại ca thử lại câu khác nhé!";
            }

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty())
                return "Tôi không tìm thấy câu trả lời phù hợp.";

            Map contentMap = (Map) candidates.get(0).get("content");
            if (contentMap == null || !contentMap.containsKey("parts"))
                return "Hệ thống đang bận xử lý dữ liệu.";

            List<Map> parts = (List<Map>) contentMap.get("parts");
            if (parts == null || parts.isEmpty())
                return "Nội dung phản hồi trống.";

            return parts.get(0).get("text").toString();

        } catch (Exception e) {
            System.err.println("Lỗi AIService: " + e.getMessage());
            return "Xin lỗi bạn, hiện tại tôi không thể giúp được bạn!";
        }
    }
}