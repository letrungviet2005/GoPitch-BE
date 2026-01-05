package com.gopitch.GoPitch.domain.response.placed;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlacedDTO {
    private Long id;
    private String pitchName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
}
