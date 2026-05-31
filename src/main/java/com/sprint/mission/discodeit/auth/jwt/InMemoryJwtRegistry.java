package com.sprint.mission.discodeit.auth.jwt;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry{

  // <userId, Queue<JwtInformation>>
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  @Override
  public void registerJwtInformation(JwtInformation info) {
    UUID userId = info.getUserDto().id();

    Queue<JwtInformation> userTokens = origin.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());

    while(userTokens.size() >= maxActiveJwtCount) {
      userTokens.poll();
    }

    userTokens.offer(info);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> userTokens = origin.get(userId);
    return userTokens != null && !userTokens.isEmpty();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> info.getAccessToken().equals(accessToken));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
  }

  @Override
  public void rotateJwtInformation(UUID userId, JwtInformation newInfo) {
    Queue<JwtInformation> userTokens = origin.get(userId);
    if (userTokens != null) {
      userTokens.clear();
      userTokens.offer(newInfo);
    } else {
      registerJwtInformation(newInfo);
    }
  }

  @Override
  public void clearExpiredJwtInformation() {
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }
}
