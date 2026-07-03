package com.gopitch.GoPitch.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.request.user.UpdateUserInformationRequestDTO;
import com.gopitch.GoPitch.domain.response.user.UserResponseDTO;
import com.gopitch.GoPitch.service.UserInformationService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user-info")
public class UserInformationController {

    private final UserInformationService userInfoService;

    public UserInformationController(UserInformationService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/nearby-clubs")
    @ApiMessage("Find nearby clubs by user location")
    public ResponseEntity<List<Club>> getNearby(@RequestParam long userId, @RequestParam double radius) {
        return ResponseEntity.ok(userInfoService.findNearbyClubs(userId, radius));
    }

    @PutMapping("/profile")
    @ApiMessage("Update current user profile information")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Valid @RequestBody UpdateUserInformationRequestDTO request) {
        return ResponseEntity.ok(userInfoService.updateMyProfile(request));
    }
}
