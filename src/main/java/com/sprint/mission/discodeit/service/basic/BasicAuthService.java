package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.exception.auth.AuthenticationRequiredException;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

//  private final SessionRegistry sessionRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  public JwtInformation refreshToken(String refreshToken) {
    if(!jwtTokenProvider.validateToken(refreshToken) || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw AuthenticationRequiredException.withDetails("유효하지 않거나 만료된 리프레시 토큰입니다.");
    }
    // 토큰에서 사용자 아이디(username) 추출
    String username = jwtTokenProvider.getUsername(refreshToken);

    // DB에서 최신 회원 정보 및 권한(Role) 조회
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
    String role = userDetails.getAuthorities().iterator().next().getAuthority();

    // 새로운 엑세스 토큰 및 리프레시 토큰 발급
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", role);
    String newAccessToken = jwtTokenProvider.generateAccessToken(claims, username);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

    // 2. 새로운 토큰 생성 및 Registry 갱신 (Rotation)
    JwtInformation newInfo = JwtInformation.builder()
        .userDto(userDetails.getUserDto())
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
    jwtRegistry.rotateJwtInformation(userDetails.getUserDto().id(), newInfo);

    // 3. 생성한 객체 반환
    return newInfo;
  }

  public void expireUserSessions(UUID userId) {
//    // 1. SessionRegistry (세션 기반) 만료
//    sessionRegistry.getAllPrincipals().stream()
//        .filter(DiscodeitUserDetails.class::isInstance)
//        .map(DiscodeitUserDetails.class::cast)
//        .filter(principal -> principal.getUserDto().id().equals(userId))
//        .forEach(principal ->
//            sessionRegistry.getAllSessions(principal, false)
//                .forEach(SessionInformation::expireNow)
//        );

    // 2. JwtRegistry (토큰 기반) 만료
    jwtRegistry.invalidateJwtInformationByUserId(userId);
  }
}