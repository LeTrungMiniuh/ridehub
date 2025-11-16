package com.ridehub.user.domain;

import static com.ridehub.user.domain.AppUserTestSamples.*;
import static com.ridehub.user.domain.UserStatisticsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserStatisticsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserStatistics.class);
        UserStatistics userStatistics1 = getUserStatisticsSample1();
        UserStatistics userStatistics2 = new UserStatistics();
        assertThat(userStatistics1).isNotEqualTo(userStatistics2);

        userStatistics2.setId(userStatistics1.getId());
        assertThat(userStatistics1).isEqualTo(userStatistics2);

        userStatistics2 = getUserStatisticsSample2();
        assertThat(userStatistics1).isNotEqualTo(userStatistics2);
    }

    @Test
    void userTest() {
        UserStatistics userStatistics = getUserStatisticsRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        userStatistics.setUser(appUserBack);
        assertThat(userStatistics.getUser()).isEqualTo(appUserBack);
        assertThat(appUserBack.getStatistics()).isEqualTo(userStatistics);

        userStatistics.user(null);
        assertThat(userStatistics.getUser()).isNull();
        assertThat(appUserBack.getStatistics()).isNull();
    }
}
