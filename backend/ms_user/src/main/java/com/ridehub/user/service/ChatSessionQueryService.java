package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.repository.ChatSessionRepository;
import com.ridehub.user.service.criteria.ChatSessionCriteria;
import com.ridehub.user.service.dto.ChatSessionDTO;
import com.ridehub.user.service.mapper.ChatSessionMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ChatSession} entities in the database.
 * The main input is a {@link ChatSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ChatSessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChatSessionQueryService extends QueryService<ChatSession> {

    private static final Logger LOG = LoggerFactory.getLogger(ChatSessionQueryService.class);

    private final ChatSessionRepository chatSessionRepository;

    private final ChatSessionMapper chatSessionMapper;

    public ChatSessionQueryService(ChatSessionRepository chatSessionRepository, ChatSessionMapper chatSessionMapper) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatSessionMapper = chatSessionMapper;
    }

    /**
     * Return a {@link Page} of {@link ChatSessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ChatSessionDTO> findByCriteria(ChatSessionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ChatSession> specification = createSpecification(criteria);
        return chatSessionRepository.findAll(specification, page).map(chatSessionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChatSessionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ChatSession> specification = createSpecification(criteria);
        return chatSessionRepository.count(specification);
    }

    /**
     * Function to convert {@link ChatSessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ChatSession> createSpecification(ChatSessionCriteria criteria) {
        Specification<ChatSession> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ChatSession_.id),
                buildStringSpecification(criteria.getSessionId(), ChatSession_.sessionId),
                buildRangeSpecification(criteria.getStartedAt(), ChatSession_.startedAt),
                buildRangeSpecification(criteria.getEndedAt(), ChatSession_.endedAt),
                buildSpecification(criteria.getIsActive(), ChatSession_.isActive),
                buildStringSpecification(criteria.getContext(), ChatSession_.context),
                buildRangeSpecification(criteria.getCreatedAt(), ChatSession_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), ChatSession_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), ChatSession_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), ChatSession_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), ChatSession_.deletedBy),
                buildSpecification(criteria.getMessagesId(), root -> root.join(ChatSession_.messages, JoinType.LEFT).get(ChatMessage_.id)),
                buildSpecification(criteria.getQueriesId(), root -> root.join(ChatSession_.queries, JoinType.LEFT).get(UserQuery_.id)),
                buildSpecification(criteria.getUserId(), root -> root.join(ChatSession_.user, JoinType.LEFT).get(AppUser_.id))
            );
        }
        return specification;
    }
}
