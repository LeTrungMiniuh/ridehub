package com.ridehub.user.service;

import com.ridehub.user.service.dto.TripRecommendationDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.user.domain.TripRecommendation}.
 */
public interface TripRecommendationService {
    /**
     * Save a tripRecommendation.
     *
     * @param tripRecommendationDTO the entity to save.
     * @return the persisted entity.
     */
    TripRecommendationDTO save(TripRecommendationDTO tripRecommendationDTO);

    /**
     * Updates a tripRecommendation.
     *
     * @param tripRecommendationDTO the entity to update.
     * @return the persisted entity.
     */
    TripRecommendationDTO update(TripRecommendationDTO tripRecommendationDTO);

    /**
     * Partially updates a tripRecommendation.
     *
     * @param tripRecommendationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TripRecommendationDTO> partialUpdate(TripRecommendationDTO tripRecommendationDTO);

    /**
     * Get the "id" tripRecommendation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TripRecommendationDTO> findOne(Long id);

    /**
     * Delete the "id" tripRecommendation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
