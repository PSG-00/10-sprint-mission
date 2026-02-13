package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    public record CreateRequest(
            @NotBlank(message = "Username is required.")
            @Pattern(regexp = "^\\S+$", message = "Username cannot contain spaces.")
            String username,

            @Email
            @NotBlank(message = "Email is required.")
            @Pattern(regexp = "^\\S+$", message = "Email cannot contain spaces.")
            String email,

            @NotBlank(message = "Password is required.")
            @Size(min = 4, message = "Password must be at least 4 characters.")
            @Pattern(regexp = "^\\S+$", message = "Password cannot contain spaces.")
            String password,

            UUID profileId
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId,
            boolean Online
    ) {}

    public record UpdateRequest(
            @Pattern(regexp = "^\\S+$", message = "New username cannot contain spaces.")
            String newUsername,

            @Pattern(regexp = "^\\S+$", message = "New email cannot contain spaces.")
            @Email(message = "New email must be a valid email address.")
            String newEmail,

            @Size(min = 4, message = "New password must be at least 4 characters.")
            @Pattern(regexp = "^\\S+$", message = "New password cannot contain spaces.")
            String newPassword,

            UUID profileId,

            Boolean isProfileDeleted
    ) {}
}
