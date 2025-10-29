package com.ridehub.route.service;

import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.ScheduleTimeSlot}.
 */
public interface ScheduleTimeSlotService {
    /**
     * Save a scheduleTimeSlot.
     *
     * @param scheduleTimeSlotDTO the entity to save.
     * @return the persisted entity.
     */
    ScheduleTimeSlotDTO save(ScheduleTimeSlotDTO scheduleTimeSlotDTO);

    /**
     * Updates a scheduleTimeSlot.
     *
     * @param scheduleTimeSlotDTO the entity to update.
     * @return the persisted entity.
     */
    ScheduleTimeSlotDTO update(ScheduleTimeSlotDTO scheduleTimeSlotDTO);

    /**
     * Partially updates a scheduleTimeSlot.
     *
     * @param scheduleTimeSlotDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScheduleTimeSlotDTO> partialUpdate(ScheduleTimeSlotDTO scheduleTimeSlotDTO);

    /**
     * Get the "id" scheduleTimeSlot.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScheduleTimeSlotDTO> findOne(Long id);

    /**
     * Delete the "id" scheduleTimeSlot.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
