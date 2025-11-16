package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.repository.ChatMessageRepository;
import com.ridehub.user.service.criteria.ChatMessageCriteria;
import com.ridehub.user.service.dto.ChatMessageDTO;
import com.ridehub.user.service.mapper.ChatMessageMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ChatMessage} entities in the database.
 * The main input is a {@link ChatMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ChatMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChatMessageQueryService extends QueryService<ChatMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageQueryService.class);

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageQueryService(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    /**
     * Return a {@link List} of {@link ChatMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> findByCriteria(ChatMessageCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ChatMessage> specification = createSpecification(criteria);
        return chatMessageMapper.toDto(chatMessageRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChatMessageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ChatMessage> specification = createSpecification(criteria);
        return chatMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link ChatMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ChatMessage> createSpecification(ChatMessageCriteria criteria) {
        Specification<ChatMessage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ChatMessage_.id),
                buildStringSpecification(criteria.getMessageText(), ChatMessage_.messageText),
                buildStringSpecification(criteria.getMessageType(), ChatMessage_.messageType),
                buildRangeSpecification(criteria.getTimestamp(), ChatMessage_.timestamp),
                buildStringSpecification(criteria.getIntent(), ChatMessage_.intent),
                buildStringSpecification(criteria.getEntities(), ChatMessage_.entities),
                buildRangeSpecification(criteria.getConfidence(), ChatMessage_.confidence),
                buildRangeSpecification(criteria.getCreatedAt(), ChatMessage_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), ChatMessage_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), ChatMessage_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), ChatMessage_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), ChatMessage_.deletedBy),
                buildSpecification(criteria.getChatSessionId(), root ->
                    root.join(ChatMessage_.chatSession, JoinType.LEFT).get(ChatSession_.id)
                )
            );
        }
        return specification;
    }
}
