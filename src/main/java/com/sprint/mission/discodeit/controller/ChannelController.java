package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelDto.Response> createChannel(@Valid @RequestBody ChannelDto.CreateRequest request) {
        return ResponseEntity.ok(channelService.create(request));
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDto.Response> getChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.find(channelId));
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto.Response>> getChannelsByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}