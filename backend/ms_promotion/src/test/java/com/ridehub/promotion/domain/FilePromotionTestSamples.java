package com.ridehub.promotion.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FilePromotionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FilePromotion getFilePromotionSample1() {
        return new FilePromotion()
            .id(1L)
            .bucket("bucket1")
            .objectKey("objectKey1")
            .contentType("contentType1")
            .size(1L)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static FilePromotion getFilePromotionSample2() {
        return new FilePromotion()
            .id(2L)
            .bucket("bucket2")
            .objectKey("objectKey2")
            .contentType("contentType2")
            .size(2L)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static FilePromotion getFilePromotionRandomSampleGenerator() {
        return new FilePromotion()
            .id(longCount.incrementAndGet())
            .bucket(UUID.randomUUID().toString())
            .objectKey(UUID.randomUUID().toString())
            .contentType(UUID.randomUUID().toString())
            .size(longCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
