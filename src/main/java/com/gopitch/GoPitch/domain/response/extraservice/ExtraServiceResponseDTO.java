package com.gopitch.GoPitch.domain.response.extraservice; // Thêm 'service' vào sau 'extra'

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtraServiceResponseDTO {
    private long id;
    private String name;
    private double price;
    private String unit;
}