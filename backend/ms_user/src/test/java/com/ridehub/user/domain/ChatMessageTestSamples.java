package com.ridehub.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChatMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChatMessage getChatMessageSample1() {
        return new ChatMessage()
            .id(1L)
            .messageText("messageText1")
            .messageType("messageType1")
            .intent("intent1")
            .entities("entities1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static ChatMessage getChatMessageSample2() {
        return new ChatMessage()
            .id(2L)
            .messageText("messageText2")
            .messageType("messageType2")
            .intent("intent2")
            .entities("entities2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static ChatMessage getChatMessageRandomSampleGenerator() {
        return new ChatMessage()
            .id(longCount.incrementAndGet())
            .messageText(UUID.randomUUID().toString())
            .messageType(UUID.randomUUID().toString())
            .intent(UUID.randomUUID().toString())
            .entities(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
