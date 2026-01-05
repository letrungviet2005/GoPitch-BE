package com.gopitch.GoPitch.domain.request.club;

import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @NotNull(message = "Active status is required")
    private boolean active = true;

    // Lưu ý: @Pattern chỉ dùng cho String, LocalTime không cần
    private LocalTime timeStart;
    private LocalTime timeEnd;

    // --- THÊM 2 TRƯỜNG NÀY ĐỂ NHẬN DỮ LIỆU TỪ FE ---
    private Double latitude;
    private Double longitude;
}