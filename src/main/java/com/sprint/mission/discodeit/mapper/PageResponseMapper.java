package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {

    default <T> PageResponse<T> fromSlice(Slice<T> slice, String nextCursor) {
        if (slice == null) return null;

        return new PageResponse<>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    default <T> PageResponse<T> fromPage(Page<T> page, String nextCursor) {
        if (page == null) return null;

        return new PageResponse<>(
                page.getContent(),
                nextCursor,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
