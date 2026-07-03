package com.gopitch.GoPitch.domain.request.bill;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class BillRequestDTO {

    @NotNull
    private double price;

    @NotNull
    private long userId;

    @NotNull
    private Long clubId;

    @NotEmpty
    private List<Long> placeds;
}
