package com.ridehub.route.service;

import com.ridehub.route.service.dto.ScheduleDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Schedule}.
 */
public interface ScheduleService {
    /**
     * Save a schedule.
     *
     * @param scheduleDTO the entity to save.
     * @return the persisted entity.
     */
    ScheduleDTO save(ScheduleDTO scheduleDTO);

    /**
     * Updates a schedule.
     *
     * @param scheduleDTO the entity to update.
     * @return the persisted entity.
     */
    ScheduleDTO update(ScheduleDTO scheduleDTO);

    /**
     * Partially updates a schedule.
     *
     * @param scheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO);

    /**
     * Get the "id" schedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScheduleDTO> findOne(Long id);

    /**
     * Delete the "id" schedule.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
