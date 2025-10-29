package com.ridehub.route.service;

import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.ScheduleOccasion}.
 */
public interface ScheduleOccasionService {
    /**
     * Save a scheduleOccasion.
     *
     * @param scheduleOccasionDTO the entity to save.
     * @return the persisted entity.
     */
    ScheduleOccasionDTO save(ScheduleOccasionDTO scheduleOccasionDTO);

    /**
     * Updates a scheduleOccasion.
     *
     * @param scheduleOccasionDTO the entity to update.
     * @return the persisted entity.
     */
    ScheduleOccasionDTO update(ScheduleOccasionDTO scheduleOccasionDTO);

    /**
     * Partially updates a scheduleOccasion.
     *
     * @param scheduleOccasionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScheduleOccasionDTO> partialUpdate(ScheduleOccasionDTO scheduleOccasionDTO);

    /**
     * Get the "id" scheduleOccasion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScheduleOccasionDTO> findOne(Long id);

    /**
     * Delete the "id" scheduleOccasion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
