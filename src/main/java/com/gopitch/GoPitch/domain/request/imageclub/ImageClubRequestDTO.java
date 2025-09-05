package com.gopitch.GoPitch.domain.request.imageclub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageClubRequest {

    private long id;
    private String imageUrl;
    private long club_id;

}
