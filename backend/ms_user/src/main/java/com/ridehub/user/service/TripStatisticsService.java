package com.ridehub.user.service;

import com.ridehub.user.service.dto.TripStatisticsDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.user.domain.TripStatistics}.
 */
public interface TripStatisticsService {
    /**
     * Save a tripStatistics.
     *
     * @param tripStatisticsDTO the entity to save.
     * @return the persisted entity.
     */
    TripStatisticsDTO save(TripStatisticsDTO tripStatisticsDTO);

    /**
     * Updates a tripStatistics.
     *
     * @param tripStatisticsDTO the entity to update.
     * @return the persisted entity.
     */
    TripStatisticsDTO update(TripStatisticsDTO tripStatisticsDTO);

    /**
     * Partially updates a tripStatistics.
     *
     * @param tripStatisticsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TripStatisticsDTO> partialUpdate(TripStatisticsDTO tripStatisticsDTO);

    /**
     * Get the "id" tripStatistics.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TripStatisticsDTO> findOne(Long id);

    /**
     * Delete the "id" tripStatistics.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
