package com.gopitch.GoPitch.domain.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.gopitch.GoPitch.domain.response.user.UserSummaryDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private long id;
    private String content;
    private int rate;
    private LocalDateTime createdAt;

    private long userId;
    private String userName;
    private String userEmail;
    private int userPoint;
    private String userRole;

    private long clubId;

    private UserSummaryDTO user;
}
