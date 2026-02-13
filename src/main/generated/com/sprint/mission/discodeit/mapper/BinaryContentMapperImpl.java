package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T16:17:04+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class BinaryContentMapperImpl implements BinaryContentMapper {

    @Override
    public BinaryContentDto.Response toResponse(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        String fileName = null;
        String contentType = null;
        byte[] bytes = null;
        long size = 0L;

        id = binaryContent.getId();
        createdAt = binaryContent.getCreatedAt();
        fileName = binaryContent.getFileName();
        contentType = binaryContent.getContentType();
        byte[] bytes1 = binaryContent.getBytes();
        if ( bytes1 != null ) {
            bytes = Arrays.copyOf( bytes1, bytes1.length );
        }
        size = binaryContent.getSize();

        BinaryContentDto.Response response = new BinaryContentDto.Response( id, createdAt, fileName, contentType, bytes, size );

        return response;
    }
}
