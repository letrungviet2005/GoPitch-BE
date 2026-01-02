package com.gopitch.GoPitch.domain.response.club;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.util.List;

// Trong file ClubResponseDTO.java
@Getter
@Setter
public class ClubResponseDTO {
    private long id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String imageAvatar;
    private boolean active;
    private LocalTime timeStart;
    private LocalTime timeEnd;

    private List<PitchPriceDTO> pitchPrices;
    private List<ImageClubDTO> imageClubs;
    private List<CommentDTO> comments;

    @Data
    public static class PitchPriceDTO {
        private String name;
        private double price;
        private LocalTime timeStart;
        private LocalTime timeEnd;
    }

    @Data
    public static class ImageClubDTO {
        private String imageUrl;
    }

    @Data
    public static class CommentDTO {
        private long id;
        private String content;
        private int rate;
        private String userName;
    }
}
