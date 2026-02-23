package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;


    @Override
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto.Response> createReadStatus(
            @RequestBody @Valid ReadStatusDto.CreateRequest request) {
        ReadStatusDto.Response response = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @RequestMapping(method = RequestMethod.PATCH, value = "/{readStatusId}")
    public ResponseEntity<ReadStatusDto.Response> updateReadStatus(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody @Valid ReadStatusDto.UpdateRequest request) {
        ReadStatusDto.Response response = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/{readStatusId}")
    public ResponseEntity<ReadStatusDto.Response> findReadStatus(@PathVariable("readStatusId") UUID readStatusId) {
        ReadStatusDto.Response response = readStatusService.find(readStatusId);
        return ResponseEntity.ok(response);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto.Response>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ReadStatusDto.Response> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
