package com.ridehub.route.domain;

import static com.ridehub.route.domain.RouteTestSamples.*;
import static com.ridehub.route.domain.ScheduleOccasionTestSamples.*;
import static com.ridehub.route.domain.ScheduleTestSamples.*;
import static com.ridehub.route.domain.ScheduleTimeSlotTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Schedule.class);
        Schedule schedule1 = getScheduleSample1();
        Schedule schedule2 = new Schedule();
        assertThat(schedule1).isNotEqualTo(schedule2);

        schedule2.setId(schedule1.getId());
        assertThat(schedule1).isEqualTo(schedule2);

        schedule2 = getScheduleSample2();
        assertThat(schedule1).isNotEqualTo(schedule2);
    }

    @Test
    void timeSlotsTest() {
        Schedule schedule = getScheduleRandomSampleGenerator();
        ScheduleTimeSlot scheduleTimeSlotBack = getScheduleTimeSlotRandomSampleGenerator();

        schedule.addTimeSlots(scheduleTimeSlotBack);
        assertThat(schedule.getTimeSlots()).containsOnly(scheduleTimeSlotBack);
        assertThat(scheduleTimeSlotBack.getSchedule()).isEqualTo(schedule);

        schedule.removeTimeSlots(scheduleTimeSlotBack);
        assertThat(schedule.getTimeSlots()).doesNotContain(scheduleTimeSlotBack);
        assertThat(scheduleTimeSlotBack.getSchedule()).isNull();

        schedule.timeSlots(new HashSet<>(Set.of(scheduleTimeSlotBack)));
        assertThat(schedule.getTimeSlots()).containsOnly(scheduleTimeSlotBack);
        assertThat(scheduleTimeSlotBack.getSchedule()).isEqualTo(schedule);

        schedule.setTimeSlots(new HashSet<>());
        assertThat(schedule.getTimeSlots()).doesNotContain(scheduleTimeSlotBack);
        assertThat(scheduleTimeSlotBack.getSchedule()).isNull();
    }

    @Test
    void occasionRuleTest() {
        Schedule schedule = getScheduleRandomSampleGenerator();
        ScheduleOccasion scheduleOccasionBack = getScheduleOccasionRandomSampleGenerator();

        schedule.setOccasionRule(scheduleOccasionBack);
        assertThat(schedule.getOccasionRule()).isEqualTo(scheduleOccasionBack);

        schedule.occasionRule(null);
        assertThat(schedule.getOccasionRule()).isNull();
    }

    @Test
    void routeTest() {
        Schedule schedule = getScheduleRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        schedule.setRoute(routeBack);
        assertThat(schedule.getRoute()).isEqualTo(routeBack);

        schedule.route(null);
        assertThat(schedule.getRoute()).isNull();
    }
}
