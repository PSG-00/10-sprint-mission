package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // UserService에 있는 toDto로 userDto를 만들기 위해서 사용
    private final UserService userService;

    @Override
    public UserDto.Response login(LoginDto.LoginRequest request) {
        String username = request.username();
        String password = request.password();

        if (request.password() == null || request.password().isBlank()) {
            throw new InvalidCredentialException();
        }

//        // 테스트 환경: 유저 검증과 비밀번호 검증 분리
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UserNotFoundException(Map.of("username", username)));
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new InvalidCredentialException(Map.of("password", password));
//        }

        // 실제 운영 환경: 사용자명 존재 확인 및 비밀번호 검증을 하나의 흐름으로 처리
        User user = userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(InvalidCredentialException::new);

        return userService.toDto(user);
    }
}
