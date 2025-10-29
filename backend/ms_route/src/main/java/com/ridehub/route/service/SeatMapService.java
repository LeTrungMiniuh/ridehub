package com.ridehub.route.service;

import com.ridehub.route.service.dto.SeatMapDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.SeatMap}.
 */
public interface SeatMapService {
    /**
     * Save a seatMap.
     *
     * @param seatMapDTO the entity to save.
     * @return the persisted entity.
     */
    SeatMapDTO save(SeatMapDTO seatMapDTO);

    /**
     * Updates a seatMap.
     *
     * @param seatMapDTO the entity to update.
     * @return the persisted entity.
     */
    SeatMapDTO update(SeatMapDTO seatMapDTO);

    /**
     * Partially updates a seatMap.
     *
     * @param seatMapDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SeatMapDTO> partialUpdate(SeatMapDTO seatMapDTO);

    /**
     * Get the "id" seatMap.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SeatMapDTO> findOne(Long id);

    /**
     * Delete the "id" seatMap.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
