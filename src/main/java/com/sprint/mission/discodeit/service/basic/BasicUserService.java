package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.biarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.DatabaseConflictException;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.user.DuplicationUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
                binaryContentRepository.findById(profileId)
                        .orElseThrow(() -> BinaryContentNotFoundException.withId(profileId));

        User user = new User(username, email, password, profile);

        user.setStatus(new UserStatus(user, Instant.now()));

        try { // 레이스 컨디션
            User savedUser = userRepository.saveAndFlush(user);
            log.info("[User Created] ID: {}, Username: {}, Email: {}", savedUser.getId(), username, email);
            return toDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw DatabaseConflictException.withUser(username, email, e);
        }
    }

    @Override
    public UserDto.Response find(UUID userId) {
        UserDto.Response response = userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        log.debug("[User Found] ID: {}, Username: {}", userId, response.username());

        return response;
    }

    @Override
    public List<UserDto.Response> findAll() {
        List<UserDto.Response> users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();

        log.debug("[Users Fetched] Total Count: {}", users.size());

        return users;
    }

    @Override
    @Transactional
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request, UUID newProfileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        validateUser(user, request.newUsername(), request.newEmail());

        String oldUsername = user.getUsername();
        String oldEmail = user.getEmail();


        String encodedPassword = (request.newPassword() != null) // null일 때 암호화해서 null이 아니게 되는 것을 방지
                ? passwordEncoder.encode(request.newPassword())
                : null;

        BinaryContent newProfile = (newProfileId == null) ? null :
                binaryContentRepository.findById(newProfileId)
                        .orElseThrow(() -> BinaryContentNotFoundException.withId(newProfileId));

        // password 업데이트는 엔티티 내 메서드를 따로 만들어서 책임 분리로 개선할 여지가 있음
        user.update(request.newUsername(), request.newEmail(), encodedPassword, newProfile);

        try { // 레이스 컨디션
            User updatedUser = userRepository.saveAndFlush(user);
            log.info("[User Updated] ID: {}, Old Username: {} -> New Username: {}, Old Email: {} -> New Email: {}",
                    userId, oldUsername, updatedUser.getUsername(), oldEmail, updatedUser.getEmail());
            return toDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw DatabaseConflictException.withUser(request.newUsername(), request.newEmail(), e);
        }
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        List<UUID> myChannelIds = readStatusRepository.findChannelIdsByUserId(userId);

        userRepository.delete(user);

        if (!myChannelIds.isEmpty()) {
            channelRepository.deleteEmptyOrLonelyChannels(myChannelIds);
        }

        log.info("[User Deleted] ID: {}, Username: {}",
                userId, user.getUsername());
    }

    // validation
    private void validateUser(User user, String username, String email) {
        // 유저가 null이면 create, 있으면 update
        // 유저가 null이 아닌 경우만(update면) 값이 변경되었는지 equal로 체크해서 변경되지 않았으면 스킵
        if (email != null
                && userRepository.existsByEmail(email)
                && (user == null || !user.getEmail().equals(email))) {
            throw DuplicationUserException.withEmail(email);
        }

        if (username != null
                && userRepository.existsByUsername(username)
                && (user == null || !user.getUsername().equals(username))) {
            throw DuplicationUserException.withUserName(username);
        }
    }

    // Helper
    public UserDto.Response toDto(User user) {
        UserStatus userStatus = Optional.ofNullable(user.getStatus())
                .orElseThrow(() -> InternalServerException.dataIntegrity(
                        "유저(ID: %s)의 상태 정보가 누락되었습니다.", user.getId()
                ));

        return userMapper.toResponse(user);
    }
}
