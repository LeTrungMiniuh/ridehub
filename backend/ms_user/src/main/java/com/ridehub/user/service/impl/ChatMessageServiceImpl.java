package com.ridehub.user.service.impl;

import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.repository.ChatMessageRepository;
import com.ridehub.user.service.ChatMessageService;
import com.ridehub.user.service.dto.ChatMessageDTO;
import com.ridehub.user.service.mapper.ChatMessageMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.user.domain.ChatMessage}.
 */
@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public ChatMessageDTO save(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to save ChatMessage : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        LOG.debug("Request to save ChatMessage entity : {}", chatMessage);
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public ChatMessageDTO update(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to update ChatMessage : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    public Optional<ChatMessageDTO> partialUpdate(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to partially update ChatMessage : {}", chatMessageDTO);

        return chatMessageRepository
            .findById(chatMessageDTO.getId())
            .map(existingChatMessage -> {
                chatMessageMapper.partialUpdate(existingChatMessage, chatMessageDTO);

                return existingChatMessage;
            })
            .map(chatMessageRepository::save)
            .map(chatMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ChatMessage : {}", id);
        return chatMessageRepository.findById(id).map(chatMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> findByChatSessionId(Long sessionId) {
        LOG.debug("Request to get ChatMessages by session ID : {}", sessionId);
        return chatMessageRepository.findByChatSessionIdOrderByTimestampAsc(sessionId);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ChatMessage : {}", id);
        chatMessageRepository.deleteById(id);
    }
}
