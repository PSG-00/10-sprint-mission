package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatus;
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto.Response> createUser(
            @RequestPart("userCreateRequest") @Valid UserDto.CreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {
        UUID profileId = uploadProfile(profile);
        UserDto.CreateRequest createRequest = new UserDto.CreateRequest(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );
        UserDto.Response response = userService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}")
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable("userId") UUID userId,
            @RequestPart("userUpdateRequest") @Valid UserDto.UpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {
        UUID profileId = uploadProfile(profile);
        UserDto.UpdateRequest updateRequest = new UserDto.UpdateRequest(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                profileId != null ? profileId : request.profileId(),
                request.isProfileDeleted()
        );
        UserDto.Response response = userService.update(userId, updateRequest);
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
    public ResponseEntity<UserStatusDto.Response> updateUserStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusDto.UpdateRequest request) {
        UserStatusDto.Response response = userStatus.updateByUserId(userId, request);
        return ResponseEntity.ok(response);
    }

    private UUID uploadProfile(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) {
            return null;
        }
        if (profile.getContentType() == null || !profile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("profile must be an image file");
        }
        try {
            BinaryContentDto.Response response = binaryContentService.create(
                    new BinaryContentDto.CreateRequest(
                            profile.getOriginalFilename(),
                            profile.getContentType(),
                            profile.getBytes()
                    )
            );
            return response.id();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
