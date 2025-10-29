package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Schedule getScheduleSample1() {
        return new Schedule()
            .id(1L)
            .scheduleCode("scheduleCode1")
            .daysOfWeek("daysOfWeek1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Schedule getScheduleSample2() {
        return new Schedule()
            .id(2L)
            .scheduleCode("scheduleCode2")
            .daysOfWeek("daysOfWeek2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Schedule getScheduleRandomSampleGenerator() {
        return new Schedule()
            .id(longCount.incrementAndGet())
            .scheduleCode(UUID.randomUUID().toString())
            .daysOfWeek(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
