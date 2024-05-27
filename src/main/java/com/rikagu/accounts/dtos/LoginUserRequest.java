package com.rikagu.accounts.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rikagu.accounts.entities.User}
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class LoginUserRequest implements Serializable {
    String email;
    String username;
    @AssertTrue(message = "Email or username must be provided, but not both")
    private boolean hasEmailOrUsername() {
        return email != null ^ username != null;
    }

    @NotBlank(message = "Password is required")
    String password;
}
