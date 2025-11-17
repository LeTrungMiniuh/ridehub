package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.PricingTemplateAsserts.*;
import static com.ridehub.route.domain.PricingTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PricingTemplateMapperTest {

    private PricingTemplateMapper pricingTemplateMapper;

    @BeforeEach
    void setUp() {
        pricingTemplateMapper = new PricingTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPricingTemplateSample1();
        var actual = pricingTemplateMapper.toEntity(pricingTemplateMapper.toDto(expected));
        assertPricingTemplateAllPropertiesEquals(expected, actual);
    }
}
