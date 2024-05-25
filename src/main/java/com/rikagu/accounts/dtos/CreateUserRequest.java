package com.rikagu.accounts.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rikagu.accounts.entities.User}
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CreateUserRequest implements Serializable {
    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 15, message = "Username must be between 1 and 15 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must be alphanumeric or underscores only")
    String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password;
}
