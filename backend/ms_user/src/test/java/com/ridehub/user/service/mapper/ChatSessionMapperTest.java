package com.ridehub.user.service.mapper;

import static com.ridehub.user.domain.ChatSessionAsserts.*;
import static com.ridehub.user.domain.ChatSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatSessionMapperTest {

    private ChatSessionMapper chatSessionMapper;

    @BeforeEach
    void setUp() {
        chatSessionMapper = new ChatSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChatSessionSample1();
        var actual = chatSessionMapper.toEntity(chatSessionMapper.toDto(expected));
        assertChatSessionAllPropertiesEquals(expected, actual);
    }
}
