package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {
    @Schema(name = "UserStatusCreateRequest", description = "UserStatus 생성 정보")
    public record CreateRequest(
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID userId,
            Instant lastActiveAt
    ) {}

    @Schema(name = "UserStatusResponse", description = "UserStatus 응답 정보")
    public record Response(
            UUID id,
            UUID userId,
            Instant lastActiveAt
    ) {}

    @Schema(name = "UserStatusUpdateRequest", description = "변경할 User 온라인 상태 정보")
    public record UpdateRequest(
            @NotNull(message = "마지막 활동 시간은 필수입니다.")
            Instant newLastActiveAt
    ){}
}
