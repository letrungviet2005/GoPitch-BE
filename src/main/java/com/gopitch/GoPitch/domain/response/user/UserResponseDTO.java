package com.gopitch.GoPitch.domain.response.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private long id;
    private String name;
    private String email;
    private boolean active;
    private int point;
    private int streakCount;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private RoleInfoDTO role;
    private UserInfoDTO userInformation;

    @Getter
    @Setter
    public static class RoleInfoDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class UserInfoDTO {
        private String fullName;
        private String phoneNumber;
        private String address;
        private Double latitude;
        private Double longitude;
    }

}
