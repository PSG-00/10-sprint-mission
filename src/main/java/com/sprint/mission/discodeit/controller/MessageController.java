package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto.Response> createMessage(@Valid @RequestBody MessageDto.CreateRequest request) {
        return ResponseEntity.ok(messageService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MessageDto.Response>> getMessages(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}