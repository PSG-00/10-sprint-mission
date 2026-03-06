package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto.Response create(UserStatusDto.CreateRequest request) {
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId));
        if (userStatusRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 해당 유저의 UserStatus가 있습니다.");
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt());

        user.setStatus(userStatus);

        return userStatusMapper.toResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto.Response find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다: " + userStatusId));
    }

    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusDto.Response update(UUID userStatusId, UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다: " + userStatusId));
        userStatus.update(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatusRepository.save(userStatus));
    }

    @Override
    @Transactional
    public UserStatusDto.Response updateByUserId(UUID userId, UserStatusDto.UpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId);
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("(무결성 오류) 해당 유저는 존재하지만 UserStatus가 없습니다: " + userId));

        userStatus.update(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다: " + userStatusId));

        userStatusRepository.delete(userStatus);
    }
}
