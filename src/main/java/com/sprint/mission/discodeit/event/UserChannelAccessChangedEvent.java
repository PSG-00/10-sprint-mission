package com.sprint.mission.discodeit.event;

import java.util.UUID;

/**
 * 특정 사용자의 채널 접근 권한이 변경되었을 때(참여/탈퇴 등) 발생하는 이벤트입니다.
 */
public record UserChannelAccessChangedEvent(
    UUID userId
) {}
