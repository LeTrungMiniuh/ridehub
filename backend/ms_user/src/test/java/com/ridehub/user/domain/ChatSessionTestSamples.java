package com.ridehub.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChatSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChatSession getChatSessionSample1() {
        return new ChatSession()
            .id(1L)
            .sessionId("sessionId1")
            .context("context1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static ChatSession getChatSessionSample2() {
        return new ChatSession()
            .id(2L)
            .sessionId("sessionId2")
            .context("context2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static ChatSession getChatSessionRandomSampleGenerator() {
        return new ChatSession()
            .id(longCount.incrementAndGet())
            .sessionId(UUID.randomUUID().toString())
            .context(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
