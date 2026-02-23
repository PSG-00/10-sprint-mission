package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChannelDto {
    @Schema(name = "PublicChannelCreateRequest", description = "Public Channel 생성 정보")
    public record PublicChannelCreateRequest(
            @NotNull(message = "채널 타입은 필수입니다.")
            String name,
            String description
    ) {}

    @Schema(name = "PrivateChannelCreateRequest", description = "Private Channel 생성 정보")
    public record PrivateChannelCreateRequest(
            @NotNull(message = "비공개 채널은 참여자 목록이 필수입니다.")
            Set<UUID> participantIds
    ) {}

    @Schema(name = "ChannelResponse", description = "Channel 응답 정보")
    public record Response(
            UUID id,
            ChannelType type,
            String name,
            String description,
            List<UUID> participantIds,
            Instant lastMessageAt
    ) {}

    @Schema(name = "PublicChannelUpdateRequest", description = "수정할 Channel 정보")
    public record UpdatePublicRequest(
            @NotBlank(message = "채널 이름은 필수입니다.")
            String newName,
            String newDescription
    ){}
}
