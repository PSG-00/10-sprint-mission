package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {
    private final MessageService messageService;
    private final BinaryContentService binaryContentService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto.Response> createMessage(
            @RequestPart("messageCreateRequest") @Valid MessageDto.CreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> files){
        MessageDto.Response response = messageService.create(request, uploadMessageFiles(files));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public ResponseEntity<MessageDto.Response> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody @Valid MessageDto.UpdateRequest request) {
        MessageDto.Response response = messageService.update(messageId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/{messageId}")
    public ResponseEntity<MessageDto.Response> findMessage(@PathVariable("messageId") UUID messageId) {
        MessageDto.Response response = messageService.find(messageId);
        return ResponseEntity.ok(response);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto.Response>> findAllByChannel(@RequestParam("channelId") UUID channelId) {
        List<MessageDto.Response> response = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(response);
    }

    public List<UUID> uploadMessageFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();

        return files.stream()
                .map(file -> binaryContentService.create(binaryContentService.multipartFileToCreateRequest(file)).id())
                .toList();
    }

}
