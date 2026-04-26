package com.gopitch.GoPitch.domain.response.club;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.util.List;

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

    private List<PitchDTO> pitches;
    private List<PitchPriceDTO> pitchPrices;
    private List<ImageClubDTO> imageClubs;
    private List<CommentDTO> comments;

    @Data
    public static class PitchDTO {
        private long id;
        private String name;
        private boolean active;
    }

    @Data
    public static class PitchPriceDTO {
        private Long id; // Phải có
        private Long pitchId;
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

    private Double latitude;
    private Double longitude;
}