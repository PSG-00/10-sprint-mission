package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "Message 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = MessageDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    ResponseEntity<MessageDto.Response> createMessage(
            @Parameter(description = "Message 생성 정보") @RequestPart("messageCreateRequest") MessageDto.CreateRequest request,
            @Parameter(description = "Message 첨부 파일들") @RequestPart(value = "attachments", required = false) List<MultipartFile> files
    );

    @Operation(summary = "Message 내용 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = MessageDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    ResponseEntity<MessageDto.Response> updateMessage(
            @Parameter(description = "수정할 Message ID") @PathVariable("messageId") UUID messageId,
            @RequestBody MessageDto.UpdateRequest request
    );

    @Operation(summary = "Message 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message 조회 성공",
                    content = @Content(schema = @Schema(implementation = MessageDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    ResponseEntity<MessageDto.Response> findMessage(
            @Parameter(description = "조회할 Message ID") @PathVariable("messageId") UUID messageId
    );

    @Operation(summary = "Message 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    ResponseEntity<Void> deleteMessage(
            @Parameter(description = "삭제할 Message ID") @PathVariable("messageId") UUID messageId
    );

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<PageResponse<MessageDto.Response>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID") @RequestParam("channelId") UUID channelId,
            @Parameter(description = "페이징 정보(page, size, sort)") @ParameterObject Pageable pageable
    );
}
