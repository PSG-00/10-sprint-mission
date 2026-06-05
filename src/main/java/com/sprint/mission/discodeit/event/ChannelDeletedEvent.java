package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.Collection;
import java.util.UUID;

public record ChannelDeletedEvent(
    UUID channelId,
    ChannelType type,
    Collection<UUID> participantIds
) {}
