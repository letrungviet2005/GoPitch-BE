package com.gopitch.GoPitch.domain.response.club;

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
    private String imageUrl;
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    private String imageUrl4;
    private String imageUrl5;
    private boolean isActive = true;
    private String timeStart;
    private String timeEnd;

}
