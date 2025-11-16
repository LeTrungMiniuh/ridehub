package com.ridehub.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserStatisticsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UserStatistics getUserStatisticsSample1() {
        return new UserStatistics()
            .id(1L)
            .totalTrips(1)
            .favoriteRoutes("favoriteRoutes1")
            .preferredVehicleTypes("preferredVehicleTypes1")
            .averageTripDuration(1)
            .bookingFrequency("bookingFrequency1")
            .loyaltyPoints(1)
            .mostFrequentOrigin("mostFrequentOrigin1")
            .mostFrequentDestination("mostFrequentDestination1")
            .peakTravelTime("peakTravelTime1")
            .weekendTrips(1)
            .holidayTrips(1)
            .cancelledTrips(1)
            .preferredSeatTypes("preferredSeatTypes1")
            .monthlyTripCount(1)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static UserStatistics getUserStatisticsSample2() {
        return new UserStatistics()
            .id(2L)
            .totalTrips(2)
            .favoriteRoutes("favoriteRoutes2")
            .preferredVehicleTypes("preferredVehicleTypes2")
            .averageTripDuration(2)
            .bookingFrequency("bookingFrequency2")
            .loyaltyPoints(2)
            .mostFrequentOrigin("mostFrequentOrigin2")
            .mostFrequentDestination("mostFrequentDestination2")
            .peakTravelTime("peakTravelTime2")
            .weekendTrips(2)
            .holidayTrips(2)
            .cancelledTrips(2)
            .preferredSeatTypes("preferredSeatTypes2")
            .monthlyTripCount(2)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static UserStatistics getUserStatisticsRandomSampleGenerator() {
        return new UserStatistics()
            .id(longCount.incrementAndGet())
            .totalTrips(intCount.incrementAndGet())
            .favoriteRoutes(UUID.randomUUID().toString())
            .preferredVehicleTypes(UUID.randomUUID().toString())
            .averageTripDuration(intCount.incrementAndGet())
            .bookingFrequency(UUID.randomUUID().toString())
            .loyaltyPoints(intCount.incrementAndGet())
            .mostFrequentOrigin(UUID.randomUUID().toString())
            .mostFrequentDestination(UUID.randomUUID().toString())
            .peakTravelTime(UUID.randomUUID().toString())
            .weekendTrips(intCount.incrementAndGet())
            .holidayTrips(intCount.incrementAndGet())
            .cancelledTrips(intCount.incrementAndGet())
            .preferredSeatTypes(UUID.randomUUID().toString())
            .monthlyTripCount(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
