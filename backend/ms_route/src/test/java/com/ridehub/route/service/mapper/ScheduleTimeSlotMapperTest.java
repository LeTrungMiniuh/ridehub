package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.ScheduleTimeSlotAsserts.*;
import static com.ridehub.route.domain.ScheduleTimeSlotTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduleTimeSlotMapperTest {

    private ScheduleTimeSlotMapper scheduleTimeSlotMapper;

    @BeforeEach
    void setUp() {
        scheduleTimeSlotMapper = new ScheduleTimeSlotMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScheduleTimeSlotSample1();
        var actual = scheduleTimeSlotMapper.toEntity(scheduleTimeSlotMapper.toDto(expected));
        assertScheduleTimeSlotAllPropertiesEquals(expected, actual);
    }
}
