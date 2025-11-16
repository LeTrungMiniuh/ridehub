package com.ridehub.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TripStatisticsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TripStatistics getTripStatisticsSample1() {
        return new TripStatistics()
            .id(1L)
            .routeId(1L)
            .totalBookings(1)
            .popularSeatTypes("popularSeatTypes1")
            .peakTravelTimes("peakTravelTimes1")
            .monthlyTrend("monthlyTrend1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static TripStatistics getTripStatisticsSample2() {
        return new TripStatistics()
            .id(2L)
            .routeId(2L)
            .totalBookings(2)
            .popularSeatTypes("popularSeatTypes2")
            .peakTravelTimes("peakTravelTimes2")
            .monthlyTrend("monthlyTrend2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static TripStatistics getTripStatisticsRandomSampleGenerator() {
        return new TripStatistics()
            .id(longCount.incrementAndGet())
            .routeId(longCount.incrementAndGet())
            .totalBookings(intCount.incrementAndGet())
            .popularSeatTypes(UUID.randomUUID().toString())
            .peakTravelTimes(UUID.randomUUID().toString())
            .monthlyTrend(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
