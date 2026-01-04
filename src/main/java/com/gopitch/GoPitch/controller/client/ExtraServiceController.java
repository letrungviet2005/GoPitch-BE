package com.gopitch.GoPitch.controller.client;

import com.gopitch.GoPitch.domain.ExtraService;
import com.gopitch.GoPitch.service.ExtraServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/extraservices")
public class ExtraServiceController {
    private final ExtraServiceService extraServiceService;

    public ExtraServiceController(ExtraServiceService extraServiceService) {
        this.extraServiceService = extraServiceService;
    }

    @GetMapping("/club/{id}")
    public ResponseEntity<List<ExtraService>> getByClub(@PathVariable long id) {
        return ResponseEntity.ok(extraServiceService.getServicesByClub(id));
    }
}
