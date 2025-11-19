package com.ridehub.user.domain;

import static com.ridehub.user.domain.ChatSessionTestSamples.*;
import static com.ridehub.user.domain.UserQueryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserQueryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserQuery.class);
        UserQuery userQuery1 = getUserQuerySample1();
        UserQuery userQuery2 = new UserQuery();
        assertThat(userQuery1).isNotEqualTo(userQuery2);

        userQuery2.setId(userQuery1.getId());
        assertThat(userQuery1).isEqualTo(userQuery2);

        userQuery2 = getUserQuerySample2();
        assertThat(userQuery1).isNotEqualTo(userQuery2);
    }

    @Test
    void chatSessionTest() {
        UserQuery userQuery = getUserQueryRandomSampleGenerator();
        ChatSession chatSessionBack = getChatSessionRandomSampleGenerator();

        userQuery.setChatSession(chatSessionBack);
        assertThat(userQuery.getChatSession()).isEqualTo(chatSessionBack);

        userQuery.chatSession(null);
        assertThat(userQuery.getChatSession()).isNull();
    }
}
