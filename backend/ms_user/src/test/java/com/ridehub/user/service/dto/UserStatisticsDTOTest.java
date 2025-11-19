package com.ridehub.user.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserStatisticsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserStatisticsDTO.class);
        UserStatisticsDTO userStatisticsDTO1 = new UserStatisticsDTO();
        userStatisticsDTO1.setId(1L);
        UserStatisticsDTO userStatisticsDTO2 = new UserStatisticsDTO();
        assertThat(userStatisticsDTO1).isNotEqualTo(userStatisticsDTO2);
        userStatisticsDTO2.setId(userStatisticsDTO1.getId());
        assertThat(userStatisticsDTO1).isEqualTo(userStatisticsDTO2);
        userStatisticsDTO2.setId(2L);
        assertThat(userStatisticsDTO1).isNotEqualTo(userStatisticsDTO2);
        userStatisticsDTO1.setId(null);
        assertThat(userStatisticsDTO1).isNotEqualTo(userStatisticsDTO2);
    }
}
