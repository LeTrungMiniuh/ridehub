package com.ridehub.route.domain;

import static com.ridehub.route.domain.ScheduleTestSamples.*;
import static com.ridehub.route.domain.ScheduleTimeSlotTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleTimeSlotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleTimeSlot.class);
        ScheduleTimeSlot scheduleTimeSlot1 = getScheduleTimeSlotSample1();
        ScheduleTimeSlot scheduleTimeSlot2 = new ScheduleTimeSlot();
        assertThat(scheduleTimeSlot1).isNotEqualTo(scheduleTimeSlot2);

        scheduleTimeSlot2.setId(scheduleTimeSlot1.getId());
        assertThat(scheduleTimeSlot1).isEqualTo(scheduleTimeSlot2);

        scheduleTimeSlot2 = getScheduleTimeSlotSample2();
        assertThat(scheduleTimeSlot1).isNotEqualTo(scheduleTimeSlot2);
    }

    @Test
    void scheduleTest() {
        ScheduleTimeSlot scheduleTimeSlot = getScheduleTimeSlotRandomSampleGenerator();
        Schedule scheduleBack = getScheduleRandomSampleGenerator();

        scheduleTimeSlot.setSchedule(scheduleBack);
        assertThat(scheduleTimeSlot.getSchedule()).isEqualTo(scheduleBack);

        scheduleTimeSlot.schedule(null);
        assertThat(scheduleTimeSlot.getSchedule()).isNull();
    }
}
