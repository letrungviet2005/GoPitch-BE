package com.gopitch.GoPitch.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import com.gopitch.GoPitch.service.UserService;
import com.gopitch.GoPitch.domain.response.user.UserResponseDTO;
import org.springframework.web.bind.annotation.RestController;
import com.gopitch.GoPitch.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        // Lấy email của người đang đăng nhập từ Security Context (JWT)
        String email = SecurityUtil.getCurrentUserLogin()
                .orElse("");

        UserResponseDTO userProfile = this.userService.getProfile(email);
        return ResponseEntity.ok(userProfile);
    }
}
