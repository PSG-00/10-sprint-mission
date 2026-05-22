package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;

  // 현재 인증된 세션의 사용자 정보 반환
  @GetMapping("/me")
  public ResponseEntity<UserDto.Response> me(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    return ResponseEntity.ok(userDetails.getUserDto());
  }

  // SPA 클라이언트를 위한 CSRF 토큰 발급 및 쿠키 생성 엔드포인트
  @GetMapping("/csrf-token")
  public ResponseEntity<Map<String, String>> getCsrfToken(CsrfToken csrfToken) {
    // 지연 로딩(Deferred Token) 구조이므로 getToken()을 호출해야 실 토큰이 생성되고 쿠키가 발급됨
    log.debug("CSRF 토큰 발급 완료: {}", csrfToken.getHeaderName());

    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .body(Map.of(
            "headerName", csrfToken.getHeaderName(),
            "token", csrfToken.getToken()
        ));
  }

  // 사용자 권한 변경 API
  @PutMapping("/role")
  public ResponseEntity<UserDto.Response> updateRole(
      @RequestBody @Valid UserRoleUpdateRequest request) {
    return ResponseEntity.ok(userService.updateRole(request.userId(), request.newRole()));
  }
}