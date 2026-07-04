package com.gopitch.GoPitch.domain.response.bill;

import com.gopitch.GoPitch.domain.response.placed.PlacedDTO;

import java.util.List;
import lombok.Data;

@Data
public class BillResponseDTO {
    private Long id;
    private Double price;
    private String createdAt;
    private Long orderCode;
    private Boolean status;
    private String customerName;
    private String clubName;
    private String clubAddress;
    private List<PlacedDTO> slots; 
}
