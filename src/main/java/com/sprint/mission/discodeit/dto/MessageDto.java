package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    @Schema(name = "MessageCreateRequest", description = "Message 생성 정보")
    public record CreateRequest(
            String content,
            @NotNull(message = "유저 ID는 필수입니다.")
            UUID authorId,
            @NotNull(message = "채널 ID는 필수입니다.")
            UUID channelId

    ) {}

    @Schema(name = "MessageResponse", description = "Message 응답 정보")
    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UserDto.Response author,
            List<BinaryContentDto.Response> attachments
    ) {}

    @Schema(name = "MessageUpdateRequest", description = "수정할 Message 내용")
    public record UpdateRequest(
            String newContent
    ) {}
}
