package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduleTimeSlotTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ScheduleTimeSlot getScheduleTimeSlotSample1() {
        return new ScheduleTimeSlot()
            .id(1L)
            .slotCode("slotCode1")
            .bufferMinutes(1)
            .sequence(1)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static ScheduleTimeSlot getScheduleTimeSlotSample2() {
        return new ScheduleTimeSlot()
            .id(2L)
            .slotCode("slotCode2")
            .bufferMinutes(2)
            .sequence(2)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static ScheduleTimeSlot getScheduleTimeSlotRandomSampleGenerator() {
        return new ScheduleTimeSlot()
            .id(longCount.incrementAndGet())
            .slotCode(UUID.randomUUID().toString())
            .bufferMinutes(intCount.incrementAndGet())
            .sequence(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
