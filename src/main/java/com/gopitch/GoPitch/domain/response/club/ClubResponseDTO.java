package com.gopitch.GoPitch.domain.response.club;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubResponseDTO {
    private long id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String imageAvatar; // Changed to imageAvatar for consistency
    private boolean isActive = true;
    private LocalTime timeStart;
    private LocalTime timeEnd;

}
