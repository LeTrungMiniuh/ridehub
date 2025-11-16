package com.ridehub.route.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.ridehub.route.domain.Trip} entity with pricing templates included.
 */
public class TripWithPricingDTO extends TripDTO implements Serializable {

    private List<PricingTemplateDTO> pricingTemplates;

    public List<PricingTemplateDTO> getPricingTemplates() {
        return pricingTemplates;
    }

    public void setPricingTemplates(List<PricingTemplateDTO> pricingTemplates) {
        this.pricingTemplates = pricingTemplates;
    }

    @Override
    public String toString() {
        return "TripWithPricingDTO{" +
            "pricingTemplates=" + pricingTemplates +
            "} " + super.toString();
    }
}