package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface MessageMapper {
    @Mapping(target = "author.online", source = "author.status.online")
    @Mapping(target = "channelId", source = "channel.id")
    MessageDto.Response toResponse(Message message);
}
