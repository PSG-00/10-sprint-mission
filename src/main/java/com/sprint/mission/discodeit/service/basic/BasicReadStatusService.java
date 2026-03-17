package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto.Response create(ReadStatusDto.CreateRequest request) {
        UUID userId = request.userId();
        UUID channelId =  request.channelId();
        Instant lastReadAt = request.lastReadAt();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + channelId));

        boolean isExist = readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
        if(isExist) throw new IllegalArgumentException("이미 해당 유저와 채널 간 ReadStatus가 존재합니다.");

        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

        try { // 레이스 컨디션 발생에 따른 ReadStatus 중복 저장에 의해 발생하는 DB 예외를 성공으로 변환
            return readStatusMapper.toResponse(readStatusRepository.saveAndFlush(readStatus));
        } catch (DataIntegrityViolationException e) {
            ReadStatus existingStatus = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
                    .orElseThrow(() -> new IllegalStateException("서버 측 오류"));
            return readStatusMapper.toResponse(existingStatus);
        }
    }

    @Override
    public ReadStatusDto.Response find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .map(readStatusMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다: " + readStatusId));
    }

    @Override
    public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId);
        }
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto.Response update(UUID readStatusId, ReadStatusDto.UpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다: " + readStatusId));

        readStatus.update(request.newLastReadAt());

        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다: " + readStatusId));

        readStatusRepository.delete(readStatus);
    }
}
