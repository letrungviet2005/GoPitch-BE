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
    private String imageUrl;
    private boolean isActive = true;
    private String timeStart;
    private String timeEnd;

}
