package com.gopitch.GoPitch.domain.response.club;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubResponseDTO {
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String email;
    private String website;
    private String imageUrl;
    private boolean isActive = true;

}
