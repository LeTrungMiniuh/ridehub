package com.ridehub.user.domain;

import static com.ridehub.user.domain.AppUserTestSamples.*;
import static com.ridehub.user.domain.TripRecommendationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripRecommendationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TripRecommendation.class);
        TripRecommendation tripRecommendation1 = getTripRecommendationSample1();
        TripRecommendation tripRecommendation2 = new TripRecommendation();
        assertThat(tripRecommendation1).isNotEqualTo(tripRecommendation2);

        tripRecommendation2.setId(tripRecommendation1.getId());
        assertThat(tripRecommendation1).isEqualTo(tripRecommendation2);

        tripRecommendation2 = getTripRecommendationSample2();
        assertThat(tripRecommendation1).isNotEqualTo(tripRecommendation2);
    }

    @Test
    void userTest() {
        TripRecommendation tripRecommendation = getTripRecommendationRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        tripRecommendation.setUser(appUserBack);
        assertThat(tripRecommendation.getUser()).isEqualTo(appUserBack);

        tripRecommendation.user(null);
        assertThat(tripRecommendation.getUser()).isNull();
    }
}
