package com.ridehub.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserQueryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UserQuery getUserQuerySample1() {
        return new UserQuery()
            .id(1L)
            .queryText("queryText1")
            .queryType("queryType1")
            .parameters("parameters1")
            .responseTime(1)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static UserQuery getUserQuerySample2() {
        return new UserQuery()
            .id(2L)
            .queryText("queryText2")
            .queryType("queryType2")
            .parameters("parameters2")
            .responseTime(2)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static UserQuery getUserQueryRandomSampleGenerator() {
        return new UserQuery()
            .id(longCount.incrementAndGet())
            .queryText(UUID.randomUUID().toString())
            .queryType(UUID.randomUUID().toString())
            .parameters(UUID.randomUUID().toString())
            .responseTime(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
