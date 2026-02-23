package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {
    @Mapping(target = "online", expression = "java(userStatus.isOnline())")
    UserStatusDto.Response toResponse(UserStatus userStatus);
}
