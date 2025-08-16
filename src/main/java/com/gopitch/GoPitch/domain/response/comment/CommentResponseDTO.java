package com.gopitch.GoPitch.domain.response.club;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {
    private long id;
    private String content;
    private int rate;
    private long userId;
}
