package com.gopitch.GoPitch.domain.request.pitchprice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PitchPriceDTO {
    private String name;
    private double price;
    private String timeStart;
    private String timeEnd;
}
