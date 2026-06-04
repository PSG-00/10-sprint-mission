package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationInMemoryRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService; // 👈 Service 사용

  // GET: 알림 목록 조회 (200, 401)
  @GetMapping
  public ResponseEntity<List<NotificationDto>> getMyNotifications(
      @AuthenticationPrincipal JwtInformation jwtInformation
  ) {
    return ResponseEntity.ok(notificationService.findAllByUserId(jwtInformation.getUserDto().id()));
  }

  // DELETE: 알림 삭제 (204, 401, 403, 404)
  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @AuthenticationPrincipal JwtInformation jwtInformation,
      @PathVariable UUID notificationId
  ) {
    notificationService.delete(jwtInformation.getUserDto().id(), notificationId);
    return ResponseEntity.noContent().build();
  }
}
