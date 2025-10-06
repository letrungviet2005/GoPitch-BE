package com.gopitch.GoPitch.domain.request.imageclub;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageClubRequestDTO {

    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;



}
