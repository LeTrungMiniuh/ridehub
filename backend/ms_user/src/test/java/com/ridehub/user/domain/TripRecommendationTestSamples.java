package com.ridehub.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TripRecommendationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TripRecommendation getTripRecommendationSample1() {
        return new TripRecommendation()
            .id(1L)
            .origin("origin1")
            .destination("destination1")
            .budgetRange("budgetRange1")
            .seatPreference("seatPreference1")
            .recommendedTrips("recommendedTrips1")
            .feedbackRating(1)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static TripRecommendation getTripRecommendationSample2() {
        return new TripRecommendation()
            .id(2L)
            .origin("origin2")
            .destination("destination2")
            .budgetRange("budgetRange2")
            .seatPreference("seatPreference2")
            .recommendedTrips("recommendedTrips2")
            .feedbackRating(2)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static TripRecommendation getTripRecommendationRandomSampleGenerator() {
        return new TripRecommendation()
            .id(longCount.incrementAndGet())
            .origin(UUID.randomUUID().toString())
            .destination(UUID.randomUUID().toString())
            .budgetRange(UUID.randomUUID().toString())
            .seatPreference(UUID.randomUUID().toString())
            .recommendedTrips(UUID.randomUUID().toString())
            .feedbackRating(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
