package com.gopitch.GoPitch.domain.request.comment;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CommentRequestDTO {
    @NotBlank(message = "Content cannot be empty")
    private String content;

    @Min(1)
    @Max(5)
    private int rate;

    @NotNull
    private Long userId;

    @NotNull
    private Long clubId;
}
