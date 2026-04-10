package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<UserDto.Response> login(
            @RequestBody @Valid LoginDto.LoginRequest request,
            HttpServletRequest httpRequest) {
        UserDto.Response response = authService.login(request);

//        // 로그에서 세션 id를 남기기 위해 추가했지만 문제가 있음 jwt로 넘어가야 할 듯
//        HttpSession session = httpRequest.getSession(true);
//        session.setAttribute("userId", response.id());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
