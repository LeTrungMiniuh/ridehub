package com.ridehub.user.domain;

import static com.ridehub.user.domain.TripStatisticsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripStatisticsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TripStatistics.class);
        TripStatistics tripStatistics1 = getTripStatisticsSample1();
        TripStatistics tripStatistics2 = new TripStatistics();
        assertThat(tripStatistics1).isNotEqualTo(tripStatistics2);

        tripStatistics2.setId(tripStatistics1.getId());
        assertThat(tripStatistics1).isEqualTo(tripStatistics2);

        tripStatistics2 = getTripStatisticsSample2();
        assertThat(tripStatistics1).isNotEqualTo(tripStatistics2);
    }
}
