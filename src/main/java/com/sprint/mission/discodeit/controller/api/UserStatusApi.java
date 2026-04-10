package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "UserStatus", description = "UserStatus API")
public interface UserStatusApi {

    @Operation(summary = "UserStatus 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UserStatus 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserStatus.class))),
            @ApiResponse(responseCode = "404", description = "UserStatus를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
    })
    ResponseEntity<UserStatusDto.Response> findByUserId(
            @Parameter(description = "조회할 User ID") @PathVariable UUID userId
    );
}
