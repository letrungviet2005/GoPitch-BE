package com.gopitch.GoPitch.domain.response.role;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDTO {
    private long id;
    private String name;
    private String description;
    private boolean active;
    private List<String> permissions;
}
