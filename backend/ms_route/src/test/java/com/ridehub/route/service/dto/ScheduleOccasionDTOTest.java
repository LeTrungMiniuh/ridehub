package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleOccasionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleOccasionDTO.class);
        ScheduleOccasionDTO scheduleOccasionDTO1 = new ScheduleOccasionDTO();
        scheduleOccasionDTO1.setId(1L);
        ScheduleOccasionDTO scheduleOccasionDTO2 = new ScheduleOccasionDTO();
        assertThat(scheduleOccasionDTO1).isNotEqualTo(scheduleOccasionDTO2);
        scheduleOccasionDTO2.setId(scheduleOccasionDTO1.getId());
        assertThat(scheduleOccasionDTO1).isEqualTo(scheduleOccasionDTO2);
        scheduleOccasionDTO2.setId(2L);
        assertThat(scheduleOccasionDTO1).isNotEqualTo(scheduleOccasionDTO2);
        scheduleOccasionDTO1.setId(null);
        assertThat(scheduleOccasionDTO1).isNotEqualTo(scheduleOccasionDTO2);
    }
}
