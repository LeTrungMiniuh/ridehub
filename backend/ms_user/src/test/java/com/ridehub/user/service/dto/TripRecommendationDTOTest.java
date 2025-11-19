package com.ridehub.user.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripRecommendationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TripRecommendationDTO.class);
        TripRecommendationDTO tripRecommendationDTO1 = new TripRecommendationDTO();
        tripRecommendationDTO1.setId(1L);
        TripRecommendationDTO tripRecommendationDTO2 = new TripRecommendationDTO();
        assertThat(tripRecommendationDTO1).isNotEqualTo(tripRecommendationDTO2);
        tripRecommendationDTO2.setId(tripRecommendationDTO1.getId());
        assertThat(tripRecommendationDTO1).isEqualTo(tripRecommendationDTO2);
        tripRecommendationDTO2.setId(2L);
        assertThat(tripRecommendationDTO1).isNotEqualTo(tripRecommendationDTO2);
        tripRecommendationDTO1.setId(null);
        assertThat(tripRecommendationDTO1).isNotEqualTo(tripRecommendationDTO2);
    }
}
