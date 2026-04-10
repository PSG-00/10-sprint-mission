package com.sprint.mission.discodeit.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,

        // Page 기반일 때 사용
        // int number,

        // Cursor 기반일 때 사용
        String nextCursor,

        int size,
        boolean hasNext,
        Long totalElements
){}
