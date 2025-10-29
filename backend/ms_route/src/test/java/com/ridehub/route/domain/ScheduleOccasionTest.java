package com.ridehub.route.domain;

import static com.ridehub.route.domain.ScheduleOccasionTestSamples.*;
import static com.ridehub.route.domain.ScheduleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ScheduleOccasionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleOccasion.class);
        ScheduleOccasion scheduleOccasion1 = getScheduleOccasionSample1();
        ScheduleOccasion scheduleOccasion2 = new ScheduleOccasion();
        assertThat(scheduleOccasion1).isNotEqualTo(scheduleOccasion2);

        scheduleOccasion2.setId(scheduleOccasion1.getId());
        assertThat(scheduleOccasion1).isEqualTo(scheduleOccasion2);

        scheduleOccasion2 = getScheduleOccasionSample2();
        assertThat(scheduleOccasion1).isNotEqualTo(scheduleOccasion2);
    }

    @Test
    void scheduleTest() {
        ScheduleOccasion scheduleOccasion = getScheduleOccasionRandomSampleGenerator();
        Schedule scheduleBack = getScheduleRandomSampleGenerator();

        scheduleOccasion.addSchedule(scheduleBack);
        assertThat(scheduleOccasion.getSchedules()).containsOnly(scheduleBack);
        assertThat(scheduleBack.getOccasionRule()).isEqualTo(scheduleOccasion);

        scheduleOccasion.removeSchedule(scheduleBack);
        assertThat(scheduleOccasion.getSchedules()).doesNotContain(scheduleBack);
        assertThat(scheduleBack.getOccasionRule()).isNull();

        scheduleOccasion.schedules(new HashSet<>(Set.of(scheduleBack)));
        assertThat(scheduleOccasion.getSchedules()).containsOnly(scheduleBack);
        assertThat(scheduleBack.getOccasionRule()).isEqualTo(scheduleOccasion);

        scheduleOccasion.setSchedules(new HashSet<>());
        assertThat(scheduleOccasion.getSchedules()).doesNotContain(scheduleBack);
        assertThat(scheduleBack.getOccasionRule()).isNull();
    }
}
