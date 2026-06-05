package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;

public record ChannelUpdatedEvent(
    UUID channelId,
    ChannelType type
) {}
