package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;


    @Override
    @Transactional
    public MessageDto.Response create(MessageDto.CreateRequest request, List<UUID> attachmentIds) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + request.channelId()));

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다." + request.authorId()));

        if (channel.getType() == ChannelType.PRIVATE) {
            if (!readStatusRepository.existsByUserIdAndChannelId(request.authorId(), channel.getId())){
                throw new IllegalArgumentException("비공개 채널에는 채널 멤버만 메시지를 전송할 수 있습니다.");
            }
        }

        List<BinaryContent> attachments = binaryContentRepository.findAllById(attachmentIds);
        Message message = new Message(request.content(), channel, author, attachments);
        Message savedMessage = messageRepository.save(message);

        Instant lastMessageAt = savedMessage.getCreatedAt() == null ? Instant.now() : savedMessage.getCreatedAt();
        channel.updateLastMessageAt(lastMessageAt);

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageDto.Response find(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다: " + messageId));
    }

    @Override
    public PageResponse<MessageDto.Response> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {

        if(!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + channelId);
        }

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable cursorPageable = PageRequest.of(0, pageable.getPageSize(), sort);

        Slice<Message> messageSlice;
        if (cursor == null) {
            messageSlice = messageRepository.findLatestByChannelId(channelId, cursorPageable);
        } else {
            messageSlice = messageRepository.findAllUseCursorByChannelId(channelId, cursor, cursorPageable);
        }


         /* 아래 주석은 Hibernate의 Batch_fetch를 직접 구현한 내용임 */
//        List<Message> messages = messageSlice.getContent();
//
//        List<UUID> messageIds = messages.stream()
//                .map(Message::getId)
//                .toList();
//
//        Map<UUID, List<BinaryContent>> attachmentsMap =
//                messageRepository.findAttachmentsByMessageIds(messageIds).stream()
//                        .filter(row -> row[1] != null)
//                        .collect(Collectors.groupingBy(
//                                row -> (UUID) row[0],
//                                Collectors.mapping(
//                                        row -> (BinaryContent) row[1],
//                                        Collectors.toList()
//                                )
//                        ));
//
//        List<MessageDto.Response> content = messages.stream()
//                .map(message -> messageMapper.toResponse(
//                        message,
//                        attachmentsMap.getOrDefault(message.getId(), List.of())
//                ))
//                .toList();
//
//        Slice<MessageDto.Response> responseSlice =
//                new SliceImpl<>(content, messageSlice.getPageable(), messageSlice.hasNext());

        Slice<MessageDto.Response> responseSlice = messageSlice.map(messageMapper::toResponse);

        String nextCursor = null;
        if (responseSlice.hasNext() && !responseSlice.getContent().isEmpty()) {
            MessageDto.Response lastMessage = responseSlice.getContent()
                    .get(responseSlice.getContent().size() - 1);
            nextCursor = lastMessage.createdAt().toString();
        }


        // messageSlice를 DTO로 변환
        return pageResponseMapper.fromSlice(responseSlice, nextCursor);
    }

    @Override
    @Transactional
    public MessageDto.Response update(UUID messageId, MessageDto.UpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다: " + messageId));
        message.update(newContent);

        return messageMapper.toResponse(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다: " + messageId));
        messageRepository.delete(message);
    }
}
