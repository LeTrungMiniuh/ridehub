package com.ridehub.route.repository;

import com.ridehub.route.domain.PricingTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PricingTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PricingTemplateRepository extends JpaRepository<PricingTemplate, Long>, JpaSpecificationExecutor<PricingTemplate> {}
