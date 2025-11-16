package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.UserQuery;
import com.ridehub.user.repository.UserQueryRepository;
import com.ridehub.user.service.criteria.UserQueryCriteria;
import com.ridehub.user.service.dto.UserQueryDTO;
import com.ridehub.user.service.mapper.UserQueryMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserQuery} entities in the database.
 * The main input is a {@link UserQueryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserQueryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserQueryQueryService extends QueryService<UserQuery> {

    private static final Logger LOG = LoggerFactory.getLogger(UserQueryQueryService.class);

    private final UserQueryRepository userQueryRepository;

    private final UserQueryMapper userQueryMapper;

    public UserQueryQueryService(UserQueryRepository userQueryRepository, UserQueryMapper userQueryMapper) {
        this.userQueryRepository = userQueryRepository;
        this.userQueryMapper = userQueryMapper;
    }

    /**
     * Return a {@link List} of {@link UserQueryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserQueryDTO> findByCriteria(UserQueryCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<UserQuery> specification = createSpecification(criteria);
        return userQueryMapper.toDto(userQueryRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserQueryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UserQuery> specification = createSpecification(criteria);
        return userQueryRepository.count(specification);
    }

    /**
     * Function to convert {@link UserQueryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserQuery> createSpecification(UserQueryCriteria criteria) {
        Specification<UserQuery> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), UserQuery_.id),
                buildStringSpecification(criteria.getQueryText(), UserQuery_.queryText),
                buildStringSpecification(criteria.getQueryType(), UserQuery_.queryType),
                buildStringSpecification(criteria.getParameters(), UserQuery_.parameters),
                buildSpecification(criteria.getResponseGenerated(), UserQuery_.responseGenerated),
                buildRangeSpecification(criteria.getResponseTime(), UserQuery_.responseTime),
                buildRangeSpecification(criteria.getCreatedAt(), UserQuery_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), UserQuery_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), UserQuery_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), UserQuery_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), UserQuery_.deletedBy),
                buildSpecification(criteria.getChatSessionId(), root ->
                    root.join(UserQuery_.chatSession, JoinType.LEFT).get(ChatSession_.id)
                )
            );
        }
        return specification;
    }
}
