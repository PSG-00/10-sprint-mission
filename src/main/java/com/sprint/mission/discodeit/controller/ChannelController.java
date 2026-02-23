package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST, value = "/public")
    public ResponseEntity<ChannelDto.Response> createPublicChannel(
            @RequestBody @Valid ChannelDto.PublicChannelCreateRequest request) {
        ChannelDto.Response response = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/private")
    public ResponseEntity<ChannelDto.Response> createPrivateChannel(
            @RequestBody @Valid ChannelDto.PrivateChannelCreateRequest request) {
        ChannelDto.Response response = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @RequestMapping(method = RequestMethod.PATCH, value = "/{channelId}")
    public ResponseEntity<ChannelDto.Response> updateChannel(
            @PathVariable("channelId") UUID channelId,
            @RequestBody @Valid ChannelDto.UpdatePublicRequest request) {
        ChannelDto.Response response = channelService.update(channelId, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{channelId}")
    public ResponseEntity<ChannelDto.Response> findChannel(@PathVariable("channelId") UUID channelId) {
        ChannelDto.Response response = channelService.find(channelId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto.Response>> findAllByUser(@RequestParam("userId") UUID userId) {
        List<ChannelDto.Response> response = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // 관리자용 전체 채널 조회 API
    @RequestMapping(method = RequestMethod.GET, value = "/findAll")
    public ResponseEntity<List<ChannelDto.Response>> findAllChannels() {
        List<ChannelDto.Response> response = channelService.findAll();
        return ResponseEntity.ok(response);
    }

}
