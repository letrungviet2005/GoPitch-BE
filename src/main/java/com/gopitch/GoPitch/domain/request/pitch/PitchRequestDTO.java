package com.gopitch.GoPitch.domain.request.pitch;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import com.gopitch.GoPitch.domain.request.pitchprice.PitchPriceDTO;

@Getter
@Setter
public class PitchRequestDTO {
    private String name;
    private boolean active;
    private List<PitchPriceDTO> pitchPrices;
}
