package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleTimeSlotDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleTimeSlotDTO.class);
        ScheduleTimeSlotDTO scheduleTimeSlotDTO1 = new ScheduleTimeSlotDTO();
        scheduleTimeSlotDTO1.setId(1L);
        ScheduleTimeSlotDTO scheduleTimeSlotDTO2 = new ScheduleTimeSlotDTO();
        assertThat(scheduleTimeSlotDTO1).isNotEqualTo(scheduleTimeSlotDTO2);
        scheduleTimeSlotDTO2.setId(scheduleTimeSlotDTO1.getId());
        assertThat(scheduleTimeSlotDTO1).isEqualTo(scheduleTimeSlotDTO2);
        scheduleTimeSlotDTO2.setId(2L);
        assertThat(scheduleTimeSlotDTO1).isNotEqualTo(scheduleTimeSlotDTO2);
        scheduleTimeSlotDTO1.setId(null);
        assertThat(scheduleTimeSlotDTO1).isNotEqualTo(scheduleTimeSlotDTO2);
    }
}
