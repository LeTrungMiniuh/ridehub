package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.AppUser;
import com.ridehub.user.repository.AppUserRepository;
import com.ridehub.user.service.criteria.AppUserCriteria;
import com.ridehub.user.service.dto.AppUserDTO;
import com.ridehub.user.service.mapper.AppUserMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AppUser} entities in the database.
 * The main input is a {@link AppUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AppUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppUserQueryService extends QueryService<AppUser> {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserQueryService.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    public AppUserQueryService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    /**
     * Return a {@link List} of {@link AppUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AppUserDTO> findByCriteria(AppUserCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserMapper.toDto(appUserRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppUserCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserRepository.count(specification);
    }

    /**
     * Function to convert {@link AppUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AppUser> createSpecification(AppUserCriteria criteria) {
        Specification<AppUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), AppUser_.id),
                buildSpecification(criteria.getKeycloakId(), AppUser_.keycloakId),
                buildStringSpecification(criteria.getEmail(), AppUser_.email),
                buildStringSpecification(criteria.getPhoneNumber(), AppUser_.phoneNumber),
                buildStringSpecification(criteria.getFirstName(), AppUser_.firstName),
                buildStringSpecification(criteria.getLastName(), AppUser_.lastName),
                buildRangeSpecification(criteria.getDateOfBirth(), AppUser_.dateOfBirth),
                buildSpecification(criteria.getIsVerified(), AppUser_.isVerified),
                buildSpecification(criteria.getIsActive(), AppUser_.isActive),
                buildRangeSpecification(criteria.getLastLoginAt(), AppUser_.lastLoginAt),
                buildRangeSpecification(criteria.getCreatedAt(), AppUser_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), AppUser_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), AppUser_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), AppUser_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), AppUser_.deletedBy),
                buildSpecification(criteria.getProfileId(), root -> root.join(AppUser_.profile, JoinType.LEFT).get(Profile_.id)),
                buildSpecification(criteria.getStatisticsId(), root -> root.join(AppUser_.statistics, JoinType.LEFT).get(UserStatistics_.id)
                ),
                buildSpecification(criteria.getChatSessionsId(), root ->
                    root.join(AppUser_.chatSessions, JoinType.LEFT).get(ChatSession_.id)
                ),
                buildSpecification(criteria.getRecommendationsId(), root ->
                    root.join(AppUser_.recommendations, JoinType.LEFT).get(TripRecommendation_.id)
                )
            );
        }
        return specification;
    }
}
