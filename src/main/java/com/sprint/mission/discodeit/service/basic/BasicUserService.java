package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public UserDto.Response create(UserDto.CreateRequest request, UUID profileId) {
        String username = request.username();
        String email = request.email();
        validateUser(null, username, email);
        String password = passwordEncoder.encode(request.password());

        BinaryContent profile = (profileId == null) ? null :
                binaryContentRepository.findById(profileId).orElse(null);

        User user = new User(username, email, password, profile);

        user.setStatus(new UserStatus(user, Instant.now()));

        try { // 레이스컨디션으로 발생할 수 있는 DB 예외를 서비스에서 처리
            return toDto(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("서버 측 오류");
        }
    }

    @Override
    public UserDto.Response find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request, UUID newProfileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        validateUser(user, request.newUsername(), request.newEmail());

        String encodedPassword = (request.newPassword() != null) // null일 때 암호화해서 null이 아니게 되는 것을 방지
                ? passwordEncoder.encode(request.newPassword())
                : null;

        BinaryContent newProfile = (newProfileId == null) ? null :
                binaryContentRepository.findById(newProfileId).orElse(null);

        // password 업데이트는 엔티티 내 메서드를 따로 만들어서 책임 분리로 개선할 여지가 있음
        user.update(request.newUsername(), request.newEmail(), encodedPassword, newProfile);

        try {
            return toDto(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("서버 측 오류");
        }


    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        List<UUID> myChannelIds = readStatusRepository.findChannelIdsByUserId(userId);

        userRepository.delete(user);

        if (!myChannelIds.isEmpty()) {
            channelRepository.deleteEmptyOrLonelyChannels(myChannelIds);
        }
    }

    // validation
    private void validateUser(User user, String username, String email) {
        // 유저가 null이면 create, 있으면 update
        // 유저가 null이 아닌 경우만(update면) 값이 변경되었는지 equal로 체크해서 변경되지 않았으면 스킵
        if (email != null
                && userRepository.existsByEmail(email)
                && (user == null || !user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        if (username != null
                && userRepository.existsByUsername(username)
                && (user == null || !user.getUsername().equals(username))) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }
    }

    // Helper
    public UserDto.Response toDto(User user) {
        UserStatus userStatus = Optional.ofNullable(user.getStatus())
                .orElseThrow(() -> new IllegalStateException("무결성 오류! 해당 유저의 상태를 찾을 수 없음!" + user.getId()));

        return userMapper.toResponse(user, userStatus.isOnline());
    }
}
