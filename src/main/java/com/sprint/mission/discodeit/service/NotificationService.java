package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationService {
  List<NotificationDto> findAllByUserId(UUID userId);

  // ⭐️ 알림 생성 시 캐시 무효화
  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  void create(UUID userId, String title, String content);

  void delete(UUID userId, UUID notificationId);
}
