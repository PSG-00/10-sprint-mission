package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserStatusApi;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/userStatuses")
@RequiredArgsConstructor
public class UserStatusController implements UserStatusApi {

    private final UserStatusRepository userStatusRepository;

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<UserStatus> findByUserId(@PathVariable UUID userId) {

        return userStatusRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
