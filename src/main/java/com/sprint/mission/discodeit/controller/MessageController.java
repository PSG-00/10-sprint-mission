package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final BinaryContentService binaryContentService;

    /**
     * @param request      메시지 본문 및 관련 ID (JSON)
     * @param attachments  첨부파일 리스트 (선택 사항)
     */

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDto.Response> createMessage(
            @RequestPart("messageCreateRequest") @Valid MessageDto.CreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments){
        List<UUID> attachmentIds = uploadAttachments(attachments);
        MessageDto.CreateRequest createRequest = new MessageDto.CreateRequest(
                request.content(),
                request.authorId(),
                request.channelId(),
                attachmentIds.isEmpty() ? request.attachmentIds() : attachmentIds
        );
        MessageDto.Response response = messageService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public ResponseEntity<MessageDto.Response> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody @Valid MessageDto.UpdateRequest request) {
        MessageDto.Response response = messageService.update(messageId, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{messageId}")
    public ResponseEntity<MessageDto.Response> findMessage(@PathVariable("messageId") UUID messageId) {
        MessageDto.Response response = messageService.find(messageId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto.Response>> findAllByChannel(@RequestParam("channelId") UUID channelId) {
        List<MessageDto.Response> response = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(response);
    }

    private List<UUID> uploadAttachments(List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }
        return attachments.stream().map(this::uploadSingleAttachment).toList();
    }

    private UUID uploadSingleAttachment(MultipartFile file) {
        try {
            BinaryContentDto.Response response = binaryContentService.create(
                    new BinaryContentDto.CreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    )
            );
            return response.id();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
