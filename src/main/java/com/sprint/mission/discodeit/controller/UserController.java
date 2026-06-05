package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.exception.etc.InvalidFileTypeException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatus;
    private final BinaryContentService binaryContentService;

    @Override
    public ResponseEntity<UserDto.Response> createUser(UserDto.CreateRequest request, MultipartFile profile) {
        UUID profileId = uploadProfile(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request, profileId));
    }

    @Override
    public ResponseEntity<UserDto.Response> updateUser(UUID userId, UserDto.UpdateRequest request, MultipartFile profile) {
        UUID profileId = uploadProfile(profile);
        return ResponseEntity.ok(userService.update(userId, request, profileId));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserDto.Response> findUser(UUID userId) {
        return ResponseEntity.ok(userService.find(userId));
    }

    @Override
    public ResponseEntity<List<UserDto.Response>> findAllUser() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    public ResponseEntity<UserStatusDto.Response> patchUserStatus(UUID userId, UserStatusDto.UpdateRequest request) {
        return ResponseEntity.ok(userStatus.updateByUserId(userId, request));
    }

    @Override
    public ResponseEntity<UserDto.Response> updateUserRole(UUID userId, UserRoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateRole(userId, request.newRole()));
    }

    // --- Private Helpers ---

    private UUID uploadProfile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw InvalidFileTypeException.imageOnly(file.getContentType());
        }

        return binaryContentService.create(binaryContentService.multipartFileToCreateRequest(file)).id();
    }
}
