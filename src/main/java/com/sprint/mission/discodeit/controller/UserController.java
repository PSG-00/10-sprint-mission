package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(@Valid @RequestBody UserDto.CreateRequest request) {
        // 서비스가 DTO를 반환한다고 가정
        UserDto.Response response = userService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.Response> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.find(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        return ResponseEntity.ok(userService.update(userId, request));
    }
}