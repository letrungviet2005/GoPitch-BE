
package com.gopitch.GoPitch.domain.request.placed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacedRequestDTO {
    private long id;
    private boolean status;
    private long bill_id;
    private long calendar_id;

}
