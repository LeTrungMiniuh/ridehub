package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.ScheduleAsserts.*;
import static com.ridehub.route.domain.ScheduleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduleMapperTest {

    private ScheduleMapper scheduleMapper;

    @BeforeEach
    void setUp() {
        scheduleMapper = new ScheduleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScheduleSample1();
        var actual = scheduleMapper.toEntity(scheduleMapper.toDto(expected));
        assertScheduleAllPropertiesEquals(expected, actual);
    }
}
