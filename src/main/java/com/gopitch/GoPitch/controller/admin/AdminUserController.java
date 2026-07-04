package com.gopitch.GoPitch.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.domain.request.user.CreateUserRequestDTO;
import com.gopitch.GoPitch.domain.request.user.UpdateUserRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.user.UserResponseDTO;
import com.gopitch.GoPitch.service.UserService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ApiMessage("Fetch users for admin")
    public ResponseEntity<ResultPaginationDTO<UserResponseDTO>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getUserSummaries(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch user detail for admin")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @ApiMessage("Create user for admin")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.createUser(requestDTO));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update user for admin")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable long id,
            @Valid @RequestBody UpdateUserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    @PatchMapping("/{id}/active")
    @ApiMessage("Activate or deactivate user")
    public ResponseEntity<UserResponseDTO> setActive(
            @PathVariable long id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(userService.setUserActive(id, active));
    }
}
