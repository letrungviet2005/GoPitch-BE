package com.gopitch.GoPitch.domain.request.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommetRequestDTO {
    private long id;
    private String content;
    private int rate;
    private long userId;
    private long clubId;

}
