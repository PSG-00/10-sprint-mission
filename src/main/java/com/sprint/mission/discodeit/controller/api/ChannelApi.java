package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.ChannelDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.Response.class)))
    ResponseEntity<ChannelDto.Response> createPublicChannel(
            @RequestBody ChannelDto.PublicChannelCreateRequest request
    );

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.Response.class)))
    ResponseEntity<ChannelDto.Response> createPrivateChannel(
            @RequestBody ChannelDto.PrivateChannelCreateRequest request
    );

    @Operation(summary = "Channel 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = ChannelDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    ResponseEntity<ChannelDto.Response> updateChannel(
            @Parameter(description = "수정할 Channel ID") @PathVariable("channelId") UUID channelId,
            @RequestBody ChannelDto.UpdatePublicRequest request
    );

    @Operation(summary = "Channel 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 Channel ID") @PathVariable("channelId") UUID channelId
    );

    @Operation(summary = "Channel 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChannelDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    ResponseEntity<ChannelDto.Response> findChannel(
            @Parameter(description = "조회할 Channel ID") @PathVariable("channelId") UUID channelId
    );

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ChannelDto.Response.class)))
    ResponseEntity<List<ChannelDto.Response>> findAllByUser(
            @Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId
    );

    @Operation(summary = "전체 Channel 목록 조회 (관리자용)")
    @ApiResponse(responseCode = "200", description = "전체 Channel 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ChannelDto.Response.class)))
    ResponseEntity<List<ChannelDto.Response>> findAllChannels();
}
