package com.ridehub.user.domain;

import static com.ridehub.user.domain.AppUserTestSamples.*;
import static com.ridehub.user.domain.ChatMessageTestSamples.*;
import static com.ridehub.user.domain.ChatSessionTestSamples.*;
import static com.ridehub.user.domain.UserQueryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ChatSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatSession.class);
        ChatSession chatSession1 = getChatSessionSample1();
        ChatSession chatSession2 = new ChatSession();
        assertThat(chatSession1).isNotEqualTo(chatSession2);

        chatSession2.setId(chatSession1.getId());
        assertThat(chatSession1).isEqualTo(chatSession2);

        chatSession2 = getChatSessionSample2();
        assertThat(chatSession1).isNotEqualTo(chatSession2);
    }

    @Test
    void messagesTest() {
        ChatSession chatSession = getChatSessionRandomSampleGenerator();
        ChatMessage chatMessageBack = getChatMessageRandomSampleGenerator();

        chatSession.addMessages(chatMessageBack);
        assertThat(chatSession.getMessages()).containsOnly(chatMessageBack);
        assertThat(chatMessageBack.getChatSession()).isEqualTo(chatSession);

        chatSession.removeMessages(chatMessageBack);
        assertThat(chatSession.getMessages()).doesNotContain(chatMessageBack);
        assertThat(chatMessageBack.getChatSession()).isNull();

        chatSession.messages(new HashSet<>(Set.of(chatMessageBack)));
        assertThat(chatSession.getMessages()).containsOnly(chatMessageBack);
        assertThat(chatMessageBack.getChatSession()).isEqualTo(chatSession);

        chatSession.setMessages(new HashSet<>());
        assertThat(chatSession.getMessages()).doesNotContain(chatMessageBack);
        assertThat(chatMessageBack.getChatSession()).isNull();
    }

    @Test
    void queriesTest() {
        ChatSession chatSession = getChatSessionRandomSampleGenerator();
        UserQuery userQueryBack = getUserQueryRandomSampleGenerator();

        chatSession.addQueries(userQueryBack);
        assertThat(chatSession.getQueries()).containsOnly(userQueryBack);
        assertThat(userQueryBack.getChatSession()).isEqualTo(chatSession);

        chatSession.removeQueries(userQueryBack);
        assertThat(chatSession.getQueries()).doesNotContain(userQueryBack);
        assertThat(userQueryBack.getChatSession()).isNull();

        chatSession.queries(new HashSet<>(Set.of(userQueryBack)));
        assertThat(chatSession.getQueries()).containsOnly(userQueryBack);
        assertThat(userQueryBack.getChatSession()).isEqualTo(chatSession);

        chatSession.setQueries(new HashSet<>());
        assertThat(chatSession.getQueries()).doesNotContain(userQueryBack);
        assertThat(userQueryBack.getChatSession()).isNull();
    }

    @Test
    void userTest() {
        ChatSession chatSession = getChatSessionRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        chatSession.setUser(appUserBack);
        assertThat(chatSession.getUser()).isEqualTo(appUserBack);

        chatSession.user(null);
        assertThat(chatSession.getUser()).isNull();
    }
}
