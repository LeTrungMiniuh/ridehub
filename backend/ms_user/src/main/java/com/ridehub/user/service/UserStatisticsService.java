package com.ridehub.user.service;

import com.ridehub.user.service.dto.UserStatisticsDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.user.domain.UserStatistics}.
 */
public interface UserStatisticsService {
    /**
     * Save a userStatistics.
     *
     * @param userStatisticsDTO the entity to save.
     * @return the persisted entity.
     */
    UserStatisticsDTO save(UserStatisticsDTO userStatisticsDTO);

    /**
     * Updates a userStatistics.
     *
     * @param userStatisticsDTO the entity to update.
     * @return the persisted entity.
     */
    UserStatisticsDTO update(UserStatisticsDTO userStatisticsDTO);

    /**
     * Partially updates a userStatistics.
     *
     * @param userStatisticsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserStatisticsDTO> partialUpdate(UserStatisticsDTO userStatisticsDTO);

    /**
     * Get all the UserStatisticsDTO where User is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<UserStatisticsDTO> findAllWhereUserIsNull();

    /**
     * Get the "id" userStatistics.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserStatisticsDTO> findOne(Long id);

    /**
     * Delete the "id" userStatistics.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
