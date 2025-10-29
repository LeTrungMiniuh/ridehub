package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.ScheduleOccasionAsserts.*;
import static com.ridehub.route.domain.ScheduleOccasionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduleOccasionMapperTest {

    private ScheduleOccasionMapper scheduleOccasionMapper;

    @BeforeEach
    void setUp() {
        scheduleOccasionMapper = new ScheduleOccasionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScheduleOccasionSample1();
        var actual = scheduleOccasionMapper.toEntity(scheduleOccasionMapper.toDto(expected));
        assertScheduleOccasionAllPropertiesEquals(expected, actual);
    }
}
