package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {
    public record CreateRequest(
            @NotNull
            UUID userid,
            Instant lastActiveAt
    ) {}

    public record UpdateRequest(
            Instant newLastActiveAt
    ){}
}
