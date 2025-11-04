package com.ridehub.booking.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Ticket getTicketSample1() {
        return new Ticket()
            .id(1L)
            .ticketCode("ticketCode1")
            .qrCode("qrCode1")
            .exchangeReason("exchangeReason1")
            .refundReason("refundReason1")
            .refundTransactionId("refundTransactionId1")
            .tripId(1L)
            .routeId(1L)
            .seatId(1L)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Ticket getTicketSample2() {
        return new Ticket()
            .id(2L)
            .ticketCode("ticketCode2")
            .qrCode("qrCode2")
            .exchangeReason("exchangeReason2")
            .refundReason("refundReason2")
            .refundTransactionId("refundTransactionId2")
            .tripId(2L)
            .routeId(2L)
            .seatId(2L)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Ticket getTicketRandomSampleGenerator() {
        return new Ticket()
            .id(longCount.incrementAndGet())
            .ticketCode(UUID.randomUUID().toString())
            .qrCode(UUID.randomUUID().toString())
            .exchangeReason(UUID.randomUUID().toString())
            .refundReason(UUID.randomUUID().toString())
            .refundTransactionId(UUID.randomUUID().toString())
            .tripId(longCount.incrementAndGet())
            .routeId(longCount.incrementAndGet())
            .seatId(longCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
