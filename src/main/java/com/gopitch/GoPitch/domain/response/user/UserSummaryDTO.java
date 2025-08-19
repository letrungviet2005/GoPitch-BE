package com.gopitch.GoPitch.domain.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private long id;
    private String name;
    private String email;
    private int point;
    private String roleName;
}
