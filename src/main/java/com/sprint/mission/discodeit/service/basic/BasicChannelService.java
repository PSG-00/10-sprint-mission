package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Transactional
    public ChannelDto.Response create(ChannelDto.PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return toDto(channelRepository.save(channel));
    }

    @Transactional
    public ChannelDto.Response create(ChannelDto.PrivateChannelCreateRequest request) {
        List<User> participants = request.participantIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId)))
                .toList();

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        participants.stream()
                .map(user -> new ReadStatus(user, savedChannel, savedChannel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        return toDto(savedChannel);
    }

    @Override
    public ChannelDto.Response find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + channelId));
    }

    @Override
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다." + userId);
        }

        return channelRepository.findAllAccessibleByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ChannelDto.Response> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ChannelDto.Response update(UUID channelId, ChannelDto.UpdatePublicRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 찾을 수 없습니다: " + channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다:" + channelId);
        }
        channel.update(request.newName(), request.newDescription());
        return toDto(channel);
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->new NoSuchElementException("해당 채널을 찾을 수 없습니다" + channelId));
        channelRepository.delete(channel);
    }

    // Helper
    private ChannelDto.Response toDto(Channel channel) {
        List<UserDto.Response> participants = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            participants = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(user -> userMapper.toResponse(user, user.getStatus().isOnline()))
                    .toList();
        }

        return channelMapper.toResponse(channel, participants);

    }

}
