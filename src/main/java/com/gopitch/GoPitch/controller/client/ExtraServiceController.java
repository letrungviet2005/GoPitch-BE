package com.gopitch.GoPitch.controller.client;

import com.gopitch.GoPitch.domain.response.extraservice.ExtraServiceResponseDTO;
import com.gopitch.GoPitch.service.ExtraServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/extra-services")
public class ExtraServiceController {
    private final ExtraServiceService extraServiceService;

    public ExtraServiceController(ExtraServiceService extraServiceService) {
        this.extraServiceService = extraServiceService;
    }

    @GetMapping("/club/{id}")
    // 2. Sửa List<ExtraService> thành List<ExtraServiceResponseDTO>
    public ResponseEntity<List<ExtraServiceResponseDTO>> getByClub(@PathVariable long id) {
        // Bây giờ kiểu dữ liệu đã khớp hoàn toàn: DTO trả về DTO
        return ResponseEntity.ok(extraServiceService.getServicesByClub(id));
    }
}