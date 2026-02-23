package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.Response create(ChannelDto.PublicChannelCreateRequest request);
    ChannelDto.Response create(ChannelDto.PrivateChannelCreateRequest request);
    ChannelDto.Response find(UUID channelId);
    List<ChannelDto.Response> findAllByUserId(UUID userId);
    List<ChannelDto.Response> findAll();
    ChannelDto.Response update(UUID channelId, ChannelDto.UpdatePublicRequest request);
    void delete(UUID channelId);
}
