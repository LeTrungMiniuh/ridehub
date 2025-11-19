package com.ridehub.user.service;

import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.service.dto.ChatMessageDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.user.domain.ChatMessage}.
 */
public interface ChatMessageService {
    /**
     * Save a chatMessage.
     *
     * @param chatMessageDTO entity to save.
     * @return persisted entity.
     */
    ChatMessageDTO save(ChatMessageDTO chatMessageDTO);

    /**
     * Save a chatMessage entity.
     *
     * @param chatMessage entity to save.
     * @return persisted entity.
     */
    ChatMessage save(ChatMessage chatMessage);

    /**
     * Updates a chatMessage.
     *
     * @param chatMessageDTO entity to update.
     * @return persisted entity.
     */
    ChatMessageDTO update(ChatMessageDTO chatMessageDTO);

    /**
     * Partially updates a chatMessage.
     *
     * @param chatMessageDTO entity to update partially.
     * @return persisted entity.
     */
    Optional<ChatMessageDTO> partialUpdate(ChatMessageDTO chatMessageDTO);

    /**
     * Get "id" chatMessage.
     *
     * @param id id of entity.
     * @return entity.
     */
    Optional<ChatMessageDTO> findOne(Long id);

    /**
     * Find all chat messages by session ID.
     *
     * @param sessionId the session ID.
     * @return list of chat messages.
     */
    List<ChatMessage> findByChatSessionId(Long sessionId);

    /**
     * Delete the "id" chatMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
