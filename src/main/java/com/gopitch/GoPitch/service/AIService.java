package com.gopitch.GoPitch.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.repository.ClubRepository;

@Service
public class AIService {

    private final ClubRepository clubRepository;

    public AIService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public String getAIResponse(String message, double lat, double lng) {
        if (message == null || message.isBlank()) {
            return "Xin chào! Tôi có thể giúp bạn tìm sân bóng gần đây. Hãy hỏi tôi về sân, giá hoặc địa điểm.";
        }

        String normalized = message.toLowerCase(Locale.ROOT);

        if (normalized.contains("gần") || normalized.contains("nearby") || normalized.contains("quanh")) {
            return buildNearbyReply(lat, lng, 10);
        }

        if (normalized.contains("giá") || normalized.contains("price")) {
            return "Giá sân phụ thuộc khung giờ và loại sân. Vui lòng chọn CLB trên bản đồ để xem bảng giá chi tiết theo từng khung giờ.";
        }

        if (normalized.contains("đặt") || normalized.contains("book")) {
            return "Để đặt sân: chọn CLB → chọn sân con → chọn khung giờ trên lịch → thanh toán qua PayOS. Sau khi thanh toán thành công, lịch sẽ được lưu vào mục Hóa đơn của bạn.";
        }

        if (normalized.contains("sân") || normalized.contains("club") || normalized.contains("clb")) {
            List<Club> clubs = clubRepository.findAll();
            if (clubs.isEmpty()) {
                return "Hiện chưa có CLB nào trong hệ thống.";
            }
            String names = clubs.stream()
                    .limit(5)
                    .map(c -> c.getName() + " (" + c.getAddress() + ")")
                    .collect(Collectors.joining("\n- ", "- ", ""));
            return "Một số CLB đang hoạt động:\n" + names;
        }

        return "Tôi là trợ lý GoPitch. Bạn có thể hỏi về: sân gần bạn, cách đặt sân, hoặc giá thuê sân.";
    }

    private String buildNearbyReply(double lat, double lng, double radiusKm) {
        if (lat == 0 && lng == 0) {
            return "Vui lòng bật định vị hoặc cung cấp tọa độ để tôi tìm sân gần bạn.";
        }

        List<Club> nearby = clubRepository.findNearestClubs(lat, lng, radiusKm);
        if (nearby.isEmpty()) {
            return "Không tìm thấy CLB nào trong bán kính " + radiusKm + " km. Thử mở rộng khu vực tìm kiếm.";
        }

        String list = nearby.stream()
                .limit(5)
                .map(c -> c.getName() + " - " + c.getAddress())
                .collect(Collectors.joining("\n- ", "- ", ""));

        return "Các CLB gần bạn (trong " + radiusKm + " km):\n" + list;
    }
}
