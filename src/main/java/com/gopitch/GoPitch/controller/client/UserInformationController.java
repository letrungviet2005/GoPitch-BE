package com.gopitch.GoPitch.controller.client;
import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.service.UserInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/user-info")
public class UserInformationController {
    private final UserInformationService userInfoService;

    public UserInformationController(UserInformationService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/nearby-clubs")
    public ResponseEntity<List<Club>> getNearby(@RequestParam long userId, @RequestParam double radius) {
        return ResponseEntity.ok(userInfoService.findNearbyClubs(userId, radius));
    }
    
    // Thêm các API updateProfile...
}
