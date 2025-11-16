package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PricingTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PricingTemplateDTO.class);
        PricingTemplateDTO pricingTemplateDTO1 = new PricingTemplateDTO();
        pricingTemplateDTO1.setId(1L);
        PricingTemplateDTO pricingTemplateDTO2 = new PricingTemplateDTO();
        assertThat(pricingTemplateDTO1).isNotEqualTo(pricingTemplateDTO2);
        pricingTemplateDTO2.setId(pricingTemplateDTO1.getId());
        assertThat(pricingTemplateDTO1).isEqualTo(pricingTemplateDTO2);
        pricingTemplateDTO2.setId(2L);
        assertThat(pricingTemplateDTO1).isNotEqualTo(pricingTemplateDTO2);
        pricingTemplateDTO1.setId(null);
        assertThat(pricingTemplateDTO1).isNotEqualTo(pricingTemplateDTO2);
    }
}
