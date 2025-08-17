package com.gopitch.GoPitch.domain.response.pitchprice;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PitchPriceResponseDTO {
    private long id;
    private String name;
    private double price;
    private LocalTime timeStart;
    private LocalTime timeEnd;
}
