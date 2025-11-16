package com.ridehub.booking.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketCriteriaTest {

    @Test
    void newTicketCriteriaHasAllFiltersNullTest() {
        var ticketCriteria = new TicketCriteria();
        assertThat(ticketCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketCriteria = new TicketCriteria();

        setAllFilters(ticketCriteria);

        assertThat(ticketCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketCriteriaCopyCreatesNullFilterTest() {
        var ticketCriteria = new TicketCriteria();
        var copy = ticketCriteria.copy();

        assertThat(ticketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCriteria)
        );
    }

    @Test
    void ticketCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketCriteria = new TicketCriteria();
        setAllFilters(ticketCriteria);

        var copy = ticketCriteria.copy();

        assertThat(ticketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketCriteria = new TicketCriteria();

        assertThat(ticketCriteria).hasToString("TicketCriteria{}");
    }

    private static void setAllFilters(TicketCriteria ticketCriteria) {
        ticketCriteria.id();
        ticketCriteria.ticketCode();
        ticketCriteria.price();
        ticketCriteria.timeFrom();
        ticketCriteria.timeTo();
        ticketCriteria.checkedIn();
        ticketCriteria.status();
        ticketCriteria.refundStatus();
        ticketCriteria.refundReason();
        ticketCriteria.refundRequestedAt();
        ticketCriteria.refundCompletedAt();
        ticketCriteria.refundAmount();
        ticketCriteria.refundTransactionId();
        ticketCriteria.tripId();
        ticketCriteria.routeId();
        ticketCriteria.seatId();
        ticketCriteria.createdAt();
        ticketCriteria.isDeleted();
        ticketCriteria.deletedBy();
        ticketCriteria.originalTicketId();
        ticketCriteria.exchangedTicketId();
        ticketCriteria.bookingId();
        ticketCriteria.exchangedFromId();
        ticketCriteria.exchangedToId();
        ticketCriteria.distinct();
    }

    private static Condition<TicketCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTicketCode()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getTimeFrom()) &&
                condition.apply(criteria.getTimeTo()) &&
                condition.apply(criteria.getCheckedIn()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRefundStatus()) &&
                condition.apply(criteria.getRefundReason()) &&
                condition.apply(criteria.getRefundRequestedAt()) &&
                condition.apply(criteria.getRefundCompletedAt()) &&
                condition.apply(criteria.getRefundAmount()) &&
                condition.apply(criteria.getRefundTransactionId()) &&
                condition.apply(criteria.getTripId()) &&
                condition.apply(criteria.getRouteId()) &&
                condition.apply(criteria.getSeatId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getOriginalTicketId()) &&
                condition.apply(criteria.getExchangedTicketId()) &&
                condition.apply(criteria.getBookingId()) &&
                condition.apply(criteria.getExchangedFromId()) &&
                condition.apply(criteria.getExchangedToId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketCriteria> copyFiltersAre(TicketCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTicketCode(), copy.getTicketCode()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getTimeFrom(), copy.getTimeFrom()) &&
                condition.apply(criteria.getTimeTo(), copy.getTimeTo()) &&
                condition.apply(criteria.getCheckedIn(), copy.getCheckedIn()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRefundStatus(), copy.getRefundStatus()) &&
                condition.apply(criteria.getRefundReason(), copy.getRefundReason()) &&
                condition.apply(criteria.getRefundRequestedAt(), copy.getRefundRequestedAt()) &&
                condition.apply(criteria.getRefundCompletedAt(), copy.getRefundCompletedAt()) &&
                condition.apply(criteria.getRefundAmount(), copy.getRefundAmount()) &&
                condition.apply(criteria.getRefundTransactionId(), copy.getRefundTransactionId()) &&
                condition.apply(criteria.getTripId(), copy.getTripId()) &&
                condition.apply(criteria.getRouteId(), copy.getRouteId()) &&
                condition.apply(criteria.getSeatId(), copy.getSeatId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getOriginalTicketId(), copy.getOriginalTicketId()) &&
                condition.apply(criteria.getExchangedTicketId(), copy.getExchangedTicketId()) &&
                condition.apply(criteria.getBookingId(), copy.getBookingId()) &&
                condition.apply(criteria.getExchangedFromId(), copy.getExchangedFromId()) &&
                condition.apply(criteria.getExchangedToId(), copy.getExchangedToId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
