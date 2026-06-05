package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record UserUpdatedEvent(
    UUID userId
) {}
