package com.gopitch.GoPitch.domain.response.pitch;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PitchResponseDTO {
    private long id;
    private long clubId;
    private String name;
    private boolean active;
}