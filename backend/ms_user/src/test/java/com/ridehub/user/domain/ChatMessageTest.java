package com.ridehub.user.domain;

import static com.ridehub.user.domain.ChatMessageTestSamples.*;
import static com.ridehub.user.domain.ChatSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChatMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatMessage.class);
        ChatMessage chatMessage1 = getChatMessageSample1();
        ChatMessage chatMessage2 = new ChatMessage();
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);

        chatMessage2.setId(chatMessage1.getId());
        assertThat(chatMessage1).isEqualTo(chatMessage2);

        chatMessage2 = getChatMessageSample2();
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);
    }

    @Test
    void chatSessionTest() {
        ChatMessage chatMessage = getChatMessageRandomSampleGenerator();
        ChatSession chatSessionBack = getChatSessionRandomSampleGenerator();

        chatMessage.setChatSession(chatSessionBack);
        assertThat(chatMessage.getChatSession()).isEqualTo(chatSessionBack);

        chatMessage.chatSession(null);
        assertThat(chatMessage.getChatSession()).isNull();
    }
}
