package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {
    @Mapping(target = "channelId", source = "channel.id")
    MessageDto.Response toResponse(Message message);

    @Mapping(target = "channelId", source = "message.channel.id")
    @Mapping(target = "attachments", source = "attachments")
    MessageDto.Response toResponse(Message message, List<BinaryContent> attachments);
}
