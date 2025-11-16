package com.ridehub.route.service;

import com.ridehub.route.service.dto.PricingTemplateDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.PricingTemplate}.
 */
public interface PricingTemplateService {
    /**
     * Save a pricingTemplate.
     *
     * @param pricingTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    PricingTemplateDTO save(PricingTemplateDTO pricingTemplateDTO);

    /**
     * Updates a pricingTemplate.
     *
     * @param pricingTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    PricingTemplateDTO update(PricingTemplateDTO pricingTemplateDTO);

    /**
     * Partially updates a pricingTemplate.
     *
     * @param pricingTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PricingTemplateDTO> partialUpdate(PricingTemplateDTO pricingTemplateDTO);

    /**
     * Get the "id" pricingTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PricingTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" pricingTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
