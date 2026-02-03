package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // Message create(MessageDto.CreateRequest request);
    MessageDto.Response create(MessageDto.CreateRequest request);
    MessageDto.Response find(UUID messageId);
    List<MessageDto.Response> findAllByChannelId(UUID channelId);
    // Message update(UUID messageId, MessageDto.UpdateRequest request);
    MessageDto.Response update(UUID messageId, MessageDto.UpdateRequest request);
    void delete(UUID messageId);
}
