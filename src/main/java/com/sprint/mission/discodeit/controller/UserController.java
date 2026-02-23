package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatus;
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.Response> createUser(
            @RequestPart("userCreateRequest") @Valid UserDto.CreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {
        UserDto.Response response = userService.create(request, uploadProfile(profile));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") @Valid UserDto.UpdateRequest request,
            @RequestPart (value = "profile", required = false) MultipartFile profile) {
        UserDto.Response response = userService.update(userId, request, uploadProfile(profile));
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    public ResponseEntity<UserDto.Response> findUser(@PathVariable("userId") UUID userId) {
        UserDto.Response response = userService.find(userId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto.Response>> findAllUser() {
        List<UserDto.Response> response = userService.findAll();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/userStatus")
    public ResponseEntity<UserStatusDto.Response> patchUserStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid UserStatusDto.UpdateRequest request) {
        UserStatusDto.Response response = userStatus.updateByUserId(userId, request);
        return ResponseEntity.ok(response);
    }

    private UUID uploadProfile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지만 업로드 가능합니다.");
        }

        return binaryContentService.create(binaryContentService.multipartFileToCreateRequest(file)).id();
    }
}
