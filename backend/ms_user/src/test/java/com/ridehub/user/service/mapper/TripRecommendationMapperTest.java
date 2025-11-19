package com.ridehub.user.service.mapper;

import static com.ridehub.user.domain.TripRecommendationAsserts.*;
import static com.ridehub.user.domain.TripRecommendationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripRecommendationMapperTest {

    private TripRecommendationMapper tripRecommendationMapper;

    @BeforeEach
    void setUp() {
        tripRecommendationMapper = new TripRecommendationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTripRecommendationSample1();
        var actual = tripRecommendationMapper.toEntity(tripRecommendationMapper.toDto(expected));
        assertTripRecommendationAllPropertiesEquals(expected, actual);
    }
}
