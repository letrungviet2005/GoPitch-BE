package com.gopitch.GoPitch.domain.response.auth;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String email;
    private String name;
    private Instant createdAt;
}
