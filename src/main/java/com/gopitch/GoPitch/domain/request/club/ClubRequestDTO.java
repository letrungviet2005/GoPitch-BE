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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubRequestDTO {
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String imageAvatar;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private boolean active;
    private Double latitude;
    private Double longitude;

    private List<PitchDTO> pitches;
    private List<ImageClubDTO> imageClubs;
    private List<ExtraServiceDTO> extraServices;
    private List<PitchPriceDTO> pitchPrices;

    @Getter
    @Setter
    public static class PitchDTO {
        private Long id;
        private String name;
        private boolean active;
        private List<PitchPriceDTO> pitchPrices;
    }

    @Getter
    @Setter
    public static class ImageClubDTO {
        private String imageUrl;
    }

    @Getter
    @Setter
    public static class PitchPriceDTO {
        private Long pitchId;
        private String name;
        private double price;
        private LocalTime timeStart;
        private LocalTime timeEnd;
    }

    @Getter
    @Setter
    public static class ExtraServiceDTO {
        private String name;
        private double price;
        private String description;
    }
}