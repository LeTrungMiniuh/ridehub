package com.ridehub.user.service;

import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.service.dto.ChatSessionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.user.domain.ChatSession}.
 */
public interface ChatSessionService {
    /**
     * Save a chatSession.
     *
     * @param chatSessionDTO entity to save.
     * @return persisted entity.
     */
    ChatSessionDTO save(ChatSessionDTO chatSessionDTO);

    /**
     * Save a chatSession entity.
     *
     * @param chatSession entity to save.
     * @return persisted entity.
     */
    ChatSession save(ChatSession chatSession);

    /**
     * Updates a chatSession.
     *
     * @param chatSessionDTO the entity to update.
     * @return the persisted entity.
     */
    ChatSessionDTO update(ChatSessionDTO chatSessionDTO);

    /**
     * Partially updates a chatSession.
     *
     * @param chatSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ChatSessionDTO> partialUpdate(ChatSessionDTO chatSessionDTO);

    /**
     * Get the "id" chatSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChatSessionDTO> findOne(Long id);

    /**
     * Delete the "id" chatSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
