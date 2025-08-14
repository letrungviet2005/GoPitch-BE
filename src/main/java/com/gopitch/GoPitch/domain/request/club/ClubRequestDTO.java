package com.gopitch.GoPitch.domain.request.club;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO cho request tạo/cập nhật Club
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Pattern(regexp = "^(\\+?[0-9]{7,15})?$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageAvatar;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    @NotNull(message = "Active status is required")
    private boolean active = true;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format, must be HH:mm")
    private String timeStart;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format, must be HH:mm")
    private String timeEnd;
}
