package com.ridehub.route.domain;

import static com.ridehub.route.domain.PricingTemplateTestSamples.*;
import static com.ridehub.route.domain.RouteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PricingTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PricingTemplate.class);
        PricingTemplate pricingTemplate1 = getPricingTemplateSample1();
        PricingTemplate pricingTemplate2 = new PricingTemplate();
        assertThat(pricingTemplate1).isNotEqualTo(pricingTemplate2);

        pricingTemplate2.setId(pricingTemplate1.getId());
        assertThat(pricingTemplate1).isEqualTo(pricingTemplate2);

        pricingTemplate2 = getPricingTemplateSample2();
        assertThat(pricingTemplate1).isNotEqualTo(pricingTemplate2);
    }

    @Test
    void routeTest() {
        PricingTemplate pricingTemplate = getPricingTemplateRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        pricingTemplate.setRoute(routeBack);
        assertThat(pricingTemplate.getRoute()).isEqualTo(routeBack);

        pricingTemplate.route(null);
        assertThat(pricingTemplate.getRoute()).isNull();
    }
}
