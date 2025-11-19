package com.ridehub.user.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripStatisticsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TripStatisticsDTO.class);
        TripStatisticsDTO tripStatisticsDTO1 = new TripStatisticsDTO();
        tripStatisticsDTO1.setId(1L);
        TripStatisticsDTO tripStatisticsDTO2 = new TripStatisticsDTO();
        assertThat(tripStatisticsDTO1).isNotEqualTo(tripStatisticsDTO2);
        tripStatisticsDTO2.setId(tripStatisticsDTO1.getId());
        assertThat(tripStatisticsDTO1).isEqualTo(tripStatisticsDTO2);
        tripStatisticsDTO2.setId(2L);
        assertThat(tripStatisticsDTO1).isNotEqualTo(tripStatisticsDTO2);
        tripStatisticsDTO1.setId(null);
        assertThat(tripStatisticsDTO1).isNotEqualTo(tripStatisticsDTO2);
    }
}
