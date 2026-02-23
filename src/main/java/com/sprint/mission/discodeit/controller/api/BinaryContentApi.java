package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

//    @Operation(summary = "프로필 이미지 업로드")
//    @ApiResponse(responseCode = "201", description = "프로필 이미지 업로드 성공",
//            content = @Content(schema = @Schema(implementation = UUID.class)))
//    ResponseEntity<UUID> uploadProfileFile(
//            @Parameter(description = "업로드할 프로필 이미지 파일") @RequestPart MultipartFile file
//    );
//
//    @Operation(summary = "메시지 첨부 파일 업로드")
//    @ApiResponse(responseCode = "201", description = "메시지 첨부 파일 업로드 성공",
//            content = @Content(schema = @Schema(implementation = UUID.class)))
//    ResponseEntity<List<UUID>> uploadMessageFiles(
//            @Parameter(description = "업로드할 메시지 첨부 파일 목록") @RequestPart("files") List<MultipartFile> files
//    );

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
                    content = @Content(schema = @Schema(implementation = BinaryContentDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")))
    })
    ResponseEntity<BinaryContentDto.Response> findBinaryContent(
            @Parameter(description = "조회할 첨부 파일 ID") @PathVariable("binaryContentId") UUID binaryContentId
    );

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = BinaryContentDto.Response.class)))
    ResponseEntity<List<BinaryContentDto.Response>> findAll(
            @Parameter(description = "조회할 첨부 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    );
}
