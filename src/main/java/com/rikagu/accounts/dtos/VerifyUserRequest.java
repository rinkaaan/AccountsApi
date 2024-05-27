package com.rikagu.accounts.dtos;

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
public class VerifyUserRequest implements Serializable {
    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Verification code is required")
    String verificationCode;
}
