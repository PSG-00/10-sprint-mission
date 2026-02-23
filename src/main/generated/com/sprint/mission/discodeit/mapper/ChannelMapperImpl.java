package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T17:02:54+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class ChannelMapperImpl implements ChannelMapper {

    @Override
    public ChannelDto.Response toResponse(Channel channel, List<UUID> participantIds) {
        if ( channel == null && participantIds == null ) {
            return null;
        }

        UUID id = null;
        ChannelType type = null;
        String name = null;
        String description = null;
        Instant lastMessageAt = null;
        if ( channel != null ) {
            id = channel.getId();
            type = channel.getType();
            name = channel.getName();
            description = channel.getDescription();
            lastMessageAt = channel.getLastMessageAt();
        }
        List<UUID> participantIds1 = null;
        List<UUID> list = participantIds;
        if ( list != null ) {
            participantIds1 = new ArrayList<UUID>( list );
        }

        ChannelDto.Response response = new ChannelDto.Response( id, type, name, description, participantIds1, lastMessageAt );

        return response;
    }
}
