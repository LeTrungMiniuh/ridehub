package com.ridehub.user.service.mapper;

import static com.ridehub.user.domain.TripStatisticsAsserts.*;
import static com.ridehub.user.domain.TripStatisticsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripStatisticsMapperTest {

    private TripStatisticsMapper tripStatisticsMapper;

    @BeforeEach
    void setUp() {
        tripStatisticsMapper = new TripStatisticsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTripStatisticsSample1();
        var actual = tripStatisticsMapper.toEntity(tripStatisticsMapper.toDto(expected));
        assertTripStatisticsAllPropertiesEquals(expected, actual);
    }
}
