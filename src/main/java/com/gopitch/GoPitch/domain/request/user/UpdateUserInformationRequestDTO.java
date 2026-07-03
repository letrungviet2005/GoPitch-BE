package com.gopitch.GoPitch.domain.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInformationRequestDTO {
    private String fullName;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
}
