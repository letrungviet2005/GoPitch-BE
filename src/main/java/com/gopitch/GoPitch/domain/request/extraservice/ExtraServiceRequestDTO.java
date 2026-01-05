package com.gopitch.GoPitch.domain.request.extraservice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtraServiceRequestDTO {
    private String name;
    private double price;
    private String unit;
    private long clubId; // Để biết dịch vụ này thuộc về sân nào
}