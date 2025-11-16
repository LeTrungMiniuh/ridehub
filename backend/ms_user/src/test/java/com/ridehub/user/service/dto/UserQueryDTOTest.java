package com.ridehub.user.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserQueryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserQueryDTO.class);
        UserQueryDTO userQueryDTO1 = new UserQueryDTO();
        userQueryDTO1.setId(1L);
        UserQueryDTO userQueryDTO2 = new UserQueryDTO();
        assertThat(userQueryDTO1).isNotEqualTo(userQueryDTO2);
        userQueryDTO2.setId(userQueryDTO1.getId());
        assertThat(userQueryDTO1).isEqualTo(userQueryDTO2);
        userQueryDTO2.setId(2L);
        assertThat(userQueryDTO1).isNotEqualTo(userQueryDTO2);
        userQueryDTO1.setId(null);
        assertThat(userQueryDTO1).isNotEqualTo(userQueryDTO2);
    }
}
