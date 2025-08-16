package com.gopitch.GoPitch.domain.response.club;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PitchPriceResponseDTO {
    private long id;
    private String name;
    private double price;
    private String timeStart;
    private String timeEnd;
}
