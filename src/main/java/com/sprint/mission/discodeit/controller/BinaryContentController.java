package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/{binaryContentId}")
    public ResponseEntity<BinaryContentDto.Response> findBinaryContent(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto.Response response = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(response);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto.Response>> findAll(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDto.Response> response = binaryContentService.findAllByIn(binaryContentIds);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto.Response response = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(response);
    }

}
