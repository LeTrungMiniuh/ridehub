package com.ridehub.booking.service.criteria;

import com.ridehub.booking.domain.enumeration.AvroTicketStatus;
import com.ridehub.booking.domain.enumeration.ExchangeStatus;
import com.ridehub.booking.domain.enumeration.RefundStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.booking.domain.Ticket} entity. This class is used
 * in {@link com.ridehub.booking.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AvroTicketStatus
     */
    public static class AvroTicketStatusFilter extends Filter<AvroTicketStatus> {

        public AvroTicketStatusFilter() {}

        public AvroTicketStatusFilter(AvroTicketStatusFilter filter) {
            super(filter);
        }

        @Override
        public AvroTicketStatusFilter copy() {
            return new AvroTicketStatusFilter(this);
        }
    }

    /**
     * Class for filtering ExchangeStatus
     */
    public static class ExchangeStatusFilter extends Filter<ExchangeStatus> {

        public ExchangeStatusFilter() {}

        public ExchangeStatusFilter(ExchangeStatusFilter filter) {
            super(filter);
        }

        @Override
        public ExchangeStatusFilter copy() {
            return new ExchangeStatusFilter(this);
        }
    }

    /**
     * Class for filtering RefundStatus
     */
    public static class RefundStatusFilter extends Filter<RefundStatus> {

        public RefundStatusFilter() {}

        public RefundStatusFilter(RefundStatusFilter filter) {
            super(filter);
        }

        @Override
        public RefundStatusFilter copy() {
            return new RefundStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter ticketCode;

    private BigDecimalFilter price;

    private StringFilter qrCode;

    private InstantFilter timeFrom;

    private InstantFilter timeTo;

    private BooleanFilter checkedIn;

    private AvroTicketStatusFilter status;

    private ExchangeStatusFilter exchangeStatus;

    private RefundStatusFilter refundStatus;

    private StringFilter exchangeReason;

    private StringFilter refundReason;

    private InstantFilter exchangeRequestedAt;

    private InstantFilter exchangeCompletedAt;

    private InstantFilter refundRequestedAt;

    private InstantFilter refundCompletedAt;

    private BigDecimalFilter refundAmount;

    private StringFilter refundTransactionId;

    private LongFilter tripId;

    private LongFilter routeId;

    private LongFilter seatId;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter qrCodeImgId;

    private LongFilter originalTicketId;

    private LongFilter exchangedTicketId;

    private LongFilter bookingId;

    private LongFilter exchangedFromId;

    private LongFilter exchangedToId;

    private Boolean distinct;

    public TicketCriteria() {}

    public TicketCriteria(TicketCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.ticketCode = other.optionalTicketCode().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.qrCode = other.optionalQrCode().map(StringFilter::copy).orElse(null);
        this.timeFrom = other.optionalTimeFrom().map(InstantFilter::copy).orElse(null);
        this.timeTo = other.optionalTimeTo().map(InstantFilter::copy).orElse(null);
        this.checkedIn = other.optionalCheckedIn().map(BooleanFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AvroTicketStatusFilter::copy).orElse(null);
        this.exchangeStatus = other.optionalExchangeStatus().map(ExchangeStatusFilter::copy).orElse(null);
        this.refundStatus = other.optionalRefundStatus().map(RefundStatusFilter::copy).orElse(null);
        this.exchangeReason = other.optionalExchangeReason().map(StringFilter::copy).orElse(null);
        this.refundReason = other.optionalRefundReason().map(StringFilter::copy).orElse(null);
        this.exchangeRequestedAt = other.optionalExchangeRequestedAt().map(InstantFilter::copy).orElse(null);
        this.exchangeCompletedAt = other.optionalExchangeCompletedAt().map(InstantFilter::copy).orElse(null);
        this.refundRequestedAt = other.optionalRefundRequestedAt().map(InstantFilter::copy).orElse(null);
        this.refundCompletedAt = other.optionalRefundCompletedAt().map(InstantFilter::copy).orElse(null);
        this.refundAmount = other.optionalRefundAmount().map(BigDecimalFilter::copy).orElse(null);
        this.refundTransactionId = other.optionalRefundTransactionId().map(StringFilter::copy).orElse(null);
        this.tripId = other.optionalTripId().map(LongFilter::copy).orElse(null);
        this.routeId = other.optionalRouteId().map(LongFilter::copy).orElse(null);
        this.seatId = other.optionalSeatId().map(LongFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.qrCodeImgId = other.optionalQrCodeImgId().map(LongFilter::copy).orElse(null);
        this.originalTicketId = other.optionalOriginalTicketId().map(LongFilter::copy).orElse(null);
        this.exchangedTicketId = other.optionalExchangedTicketId().map(LongFilter::copy).orElse(null);
        this.bookingId = other.optionalBookingId().map(LongFilter::copy).orElse(null);
        this.exchangedFromId = other.optionalExchangedFromId().map(LongFilter::copy).orElse(null);
        this.exchangedToId = other.optionalExchangedToId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTicketCode() {
        return ticketCode;
    }

    public Optional<StringFilter> optionalTicketCode() {
        return Optional.ofNullable(ticketCode);
    }

    public StringFilter ticketCode() {
        if (ticketCode == null) {
            setTicketCode(new StringFilter());
        }
        return ticketCode;
    }

    public void setTicketCode(StringFilter ticketCode) {
        this.ticketCode = ticketCode;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public StringFilter getQrCode() {
        return qrCode;
    }

    public Optional<StringFilter> optionalQrCode() {
        return Optional.ofNullable(qrCode);
    }

    public StringFilter qrCode() {
        if (qrCode == null) {
            setQrCode(new StringFilter());
        }
        return qrCode;
    }

    public void setQrCode(StringFilter qrCode) {
        this.qrCode = qrCode;
    }

    public InstantFilter getTimeFrom() {
        return timeFrom;
    }

    public Optional<InstantFilter> optionalTimeFrom() {
        return Optional.ofNullable(timeFrom);
    }

    public InstantFilter timeFrom() {
        if (timeFrom == null) {
            setTimeFrom(new InstantFilter());
        }
        return timeFrom;
    }

    public void setTimeFrom(InstantFilter timeFrom) {
        this.timeFrom = timeFrom;
    }

    public InstantFilter getTimeTo() {
        return timeTo;
    }

    public Optional<InstantFilter> optionalTimeTo() {
        return Optional.ofNullable(timeTo);
    }

    public InstantFilter timeTo() {
        if (timeTo == null) {
            setTimeTo(new InstantFilter());
        }
        return timeTo;
    }

    public void setTimeTo(InstantFilter timeTo) {
        this.timeTo = timeTo;
    }

    public BooleanFilter getCheckedIn() {
        return checkedIn;
    }

    public Optional<BooleanFilter> optionalCheckedIn() {
        return Optional.ofNullable(checkedIn);
    }

    public BooleanFilter checkedIn() {
        if (checkedIn == null) {
            setCheckedIn(new BooleanFilter());
        }
        return checkedIn;
    }

    public void setCheckedIn(BooleanFilter checkedIn) {
        this.checkedIn = checkedIn;
    }

    public AvroTicketStatusFilter getStatus() {
        return status;
    }

    public Optional<AvroTicketStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AvroTicketStatusFilter status() {
        if (status == null) {
            setStatus(new AvroTicketStatusFilter());
        }
        return status;
    }

    public void setStatus(AvroTicketStatusFilter status) {
        this.status = status;
    }

    public ExchangeStatusFilter getExchangeStatus() {
        return exchangeStatus;
    }

    public Optional<ExchangeStatusFilter> optionalExchangeStatus() {
        return Optional.ofNullable(exchangeStatus);
    }

    public ExchangeStatusFilter exchangeStatus() {
        if (exchangeStatus == null) {
            setExchangeStatus(new ExchangeStatusFilter());
        }
        return exchangeStatus;
    }

    public void setExchangeStatus(ExchangeStatusFilter exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public RefundStatusFilter getRefundStatus() {
        return refundStatus;
    }

    public Optional<RefundStatusFilter> optionalRefundStatus() {
        return Optional.ofNullable(refundStatus);
    }

    public RefundStatusFilter refundStatus() {
        if (refundStatus == null) {
            setRefundStatus(new RefundStatusFilter());
        }
        return refundStatus;
    }

    public void setRefundStatus(RefundStatusFilter refundStatus) {
        this.refundStatus = refundStatus;
    }

    public StringFilter getExchangeReason() {
        return exchangeReason;
    }

    public Optional<StringFilter> optionalExchangeReason() {
        return Optional.ofNullable(exchangeReason);
    }

    public StringFilter exchangeReason() {
        if (exchangeReason == null) {
            setExchangeReason(new StringFilter());
        }
        return exchangeReason;
    }

    public void setExchangeReason(StringFilter exchangeReason) {
        this.exchangeReason = exchangeReason;
    }

    public StringFilter getRefundReason() {
        return refundReason;
    }

    public Optional<StringFilter> optionalRefundReason() {
        return Optional.ofNullable(refundReason);
    }

    public StringFilter refundReason() {
        if (refundReason == null) {
            setRefundReason(new StringFilter());
        }
        return refundReason;
    }

    public void setRefundReason(StringFilter refundReason) {
        this.refundReason = refundReason;
    }

    public InstantFilter getExchangeRequestedAt() {
        return exchangeRequestedAt;
    }

    public Optional<InstantFilter> optionalExchangeRequestedAt() {
        return Optional.ofNullable(exchangeRequestedAt);
    }

    public InstantFilter exchangeRequestedAt() {
        if (exchangeRequestedAt == null) {
            setExchangeRequestedAt(new InstantFilter());
        }
        return exchangeRequestedAt;
    }

    public void setExchangeRequestedAt(InstantFilter exchangeRequestedAt) {
        this.exchangeRequestedAt = exchangeRequestedAt;
    }

    public InstantFilter getExchangeCompletedAt() {
        return exchangeCompletedAt;
    }

    public Optional<InstantFilter> optionalExchangeCompletedAt() {
        return Optional.ofNullable(exchangeCompletedAt);
    }

    public InstantFilter exchangeCompletedAt() {
        if (exchangeCompletedAt == null) {
            setExchangeCompletedAt(new InstantFilter());
        }
        return exchangeCompletedAt;
    }

    public void setExchangeCompletedAt(InstantFilter exchangeCompletedAt) {
        this.exchangeCompletedAt = exchangeCompletedAt;
    }

    public InstantFilter getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public Optional<InstantFilter> optionalRefundRequestedAt() {
        return Optional.ofNullable(refundRequestedAt);
    }

    public InstantFilter refundRequestedAt() {
        if (refundRequestedAt == null) {
            setRefundRequestedAt(new InstantFilter());
        }
        return refundRequestedAt;
    }

    public void setRefundRequestedAt(InstantFilter refundRequestedAt) {
        this.refundRequestedAt = refundRequestedAt;
    }

    public InstantFilter getRefundCompletedAt() {
        return refundCompletedAt;
    }

    public Optional<InstantFilter> optionalRefundCompletedAt() {
        return Optional.ofNullable(refundCompletedAt);
    }

    public InstantFilter refundCompletedAt() {
        if (refundCompletedAt == null) {
            setRefundCompletedAt(new InstantFilter());
        }
        return refundCompletedAt;
    }

    public void setRefundCompletedAt(InstantFilter refundCompletedAt) {
        this.refundCompletedAt = refundCompletedAt;
    }

    public BigDecimalFilter getRefundAmount() {
        return refundAmount;
    }

    public Optional<BigDecimalFilter> optionalRefundAmount() {
        return Optional.ofNullable(refundAmount);
    }

    public BigDecimalFilter refundAmount() {
        if (refundAmount == null) {
            setRefundAmount(new BigDecimalFilter());
        }
        return refundAmount;
    }

    public void setRefundAmount(BigDecimalFilter refundAmount) {
        this.refundAmount = refundAmount;
    }

    public StringFilter getRefundTransactionId() {
        return refundTransactionId;
    }

    public Optional<StringFilter> optionalRefundTransactionId() {
        return Optional.ofNullable(refundTransactionId);
    }

    public StringFilter refundTransactionId() {
        if (refundTransactionId == null) {
            setRefundTransactionId(new StringFilter());
        }
        return refundTransactionId;
    }

    public void setRefundTransactionId(StringFilter refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public LongFilter getTripId() {
        return tripId;
    }

    public Optional<LongFilter> optionalTripId() {
        return Optional.ofNullable(tripId);
    }

    public LongFilter tripId() {
        if (tripId == null) {
            setTripId(new LongFilter());
        }
        return tripId;
    }

    public void setTripId(LongFilter tripId) {
        this.tripId = tripId;
    }

    public LongFilter getRouteId() {
        return routeId;
    }

    public Optional<LongFilter> optionalRouteId() {
        return Optional.ofNullable(routeId);
    }

    public LongFilter routeId() {
        if (routeId == null) {
            setRouteId(new LongFilter());
        }
        return routeId;
    }

    public void setRouteId(LongFilter routeId) {
        this.routeId = routeId;
    }

    public LongFilter getSeatId() {
        return seatId;
    }

    public Optional<LongFilter> optionalSeatId() {
        return Optional.ofNullable(seatId);
    }

    public LongFilter seatId() {
        if (seatId == null) {
            setSeatId(new LongFilter());
        }
        return seatId;
    }

    public void setSeatId(LongFilter seatId) {
        this.seatId = seatId;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUIDFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<UUIDFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public UUIDFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new UUIDFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(UUIDFilter deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LongFilter getQrCodeImgId() {
        return qrCodeImgId;
    }

    public Optional<LongFilter> optionalQrCodeImgId() {
        return Optional.ofNullable(qrCodeImgId);
    }

    public LongFilter qrCodeImgId() {
        if (qrCodeImgId == null) {
            setQrCodeImgId(new LongFilter());
        }
        return qrCodeImgId;
    }

    public void setQrCodeImgId(LongFilter qrCodeImgId) {
        this.qrCodeImgId = qrCodeImgId;
    }

    public LongFilter getOriginalTicketId() {
        return originalTicketId;
    }

    public Optional<LongFilter> optionalOriginalTicketId() {
        return Optional.ofNullable(originalTicketId);
    }

    public LongFilter originalTicketId() {
        if (originalTicketId == null) {
            setOriginalTicketId(new LongFilter());
        }
        return originalTicketId;
    }

    public void setOriginalTicketId(LongFilter originalTicketId) {
        this.originalTicketId = originalTicketId;
    }

    public LongFilter getExchangedTicketId() {
        return exchangedTicketId;
    }

    public Optional<LongFilter> optionalExchangedTicketId() {
        return Optional.ofNullable(exchangedTicketId);
    }

    public LongFilter exchangedTicketId() {
        if (exchangedTicketId == null) {
            setExchangedTicketId(new LongFilter());
        }
        return exchangedTicketId;
    }

    public void setExchangedTicketId(LongFilter exchangedTicketId) {
        this.exchangedTicketId = exchangedTicketId;
    }

    public LongFilter getBookingId() {
        return bookingId;
    }

    public Optional<LongFilter> optionalBookingId() {
        return Optional.ofNullable(bookingId);
    }

    public LongFilter bookingId() {
        if (bookingId == null) {
            setBookingId(new LongFilter());
        }
        return bookingId;
    }

    public void setBookingId(LongFilter bookingId) {
        this.bookingId = bookingId;
    }

    public LongFilter getExchangedFromId() {
        return exchangedFromId;
    }

    public Optional<LongFilter> optionalExchangedFromId() {
        return Optional.ofNullable(exchangedFromId);
    }

    public LongFilter exchangedFromId() {
        if (exchangedFromId == null) {
            setExchangedFromId(new LongFilter());
        }
        return exchangedFromId;
    }

    public void setExchangedFromId(LongFilter exchangedFromId) {
        this.exchangedFromId = exchangedFromId;
    }

    public LongFilter getExchangedToId() {
        return exchangedToId;
    }

    public Optional<LongFilter> optionalExchangedToId() {
        return Optional.ofNullable(exchangedToId);
    }

    public LongFilter exchangedToId() {
        if (exchangedToId == null) {
            setExchangedToId(new LongFilter());
        }
        return exchangedToId;
    }

    public void setExchangedToId(LongFilter exchangedToId) {
        this.exchangedToId = exchangedToId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TicketCriteria that = (TicketCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ticketCode, that.ticketCode) &&
            Objects.equals(price, that.price) &&
            Objects.equals(qrCode, that.qrCode) &&
            Objects.equals(timeFrom, that.timeFrom) &&
            Objects.equals(timeTo, that.timeTo) &&
            Objects.equals(checkedIn, that.checkedIn) &&
            Objects.equals(status, that.status) &&
            Objects.equals(exchangeStatus, that.exchangeStatus) &&
            Objects.equals(refundStatus, that.refundStatus) &&
            Objects.equals(exchangeReason, that.exchangeReason) &&
            Objects.equals(refundReason, that.refundReason) &&
            Objects.equals(exchangeRequestedAt, that.exchangeRequestedAt) &&
            Objects.equals(exchangeCompletedAt, that.exchangeCompletedAt) &&
            Objects.equals(refundRequestedAt, that.refundRequestedAt) &&
            Objects.equals(refundCompletedAt, that.refundCompletedAt) &&
            Objects.equals(refundAmount, that.refundAmount) &&
            Objects.equals(refundTransactionId, that.refundTransactionId) &&
            Objects.equals(tripId, that.tripId) &&
            Objects.equals(routeId, that.routeId) &&
            Objects.equals(seatId, that.seatId) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(qrCodeImgId, that.qrCodeImgId) &&
            Objects.equals(originalTicketId, that.originalTicketId) &&
            Objects.equals(exchangedTicketId, that.exchangedTicketId) &&
            Objects.equals(bookingId, that.bookingId) &&
            Objects.equals(exchangedFromId, that.exchangedFromId) &&
            Objects.equals(exchangedToId, that.exchangedToId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            ticketCode,
            price,
            qrCode,
            timeFrom,
            timeTo,
            checkedIn,
            status,
            exchangeStatus,
            refundStatus,
            exchangeReason,
            refundReason,
            exchangeRequestedAt,
            exchangeCompletedAt,
            refundRequestedAt,
            refundCompletedAt,
            refundAmount,
            refundTransactionId,
            tripId,
            routeId,
            seatId,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            qrCodeImgId,
            originalTicketId,
            exchangedTicketId,
            bookingId,
            exchangedFromId,
            exchangedToId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTicketCode().map(f -> "ticketCode=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalQrCode().map(f -> "qrCode=" + f + ", ").orElse("") +
            optionalTimeFrom().map(f -> "timeFrom=" + f + ", ").orElse("") +
            optionalTimeTo().map(f -> "timeTo=" + f + ", ").orElse("") +
            optionalCheckedIn().map(f -> "checkedIn=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalExchangeStatus().map(f -> "exchangeStatus=" + f + ", ").orElse("") +
            optionalRefundStatus().map(f -> "refundStatus=" + f + ", ").orElse("") +
            optionalExchangeReason().map(f -> "exchangeReason=" + f + ", ").orElse("") +
            optionalRefundReason().map(f -> "refundReason=" + f + ", ").orElse("") +
            optionalExchangeRequestedAt().map(f -> "exchangeRequestedAt=" + f + ", ").orElse("") +
            optionalExchangeCompletedAt().map(f -> "exchangeCompletedAt=" + f + ", ").orElse("") +
            optionalRefundRequestedAt().map(f -> "refundRequestedAt=" + f + ", ").orElse("") +
            optionalRefundCompletedAt().map(f -> "refundCompletedAt=" + f + ", ").orElse("") +
            optionalRefundAmount().map(f -> "refundAmount=" + f + ", ").orElse("") +
            optionalRefundTransactionId().map(f -> "refundTransactionId=" + f + ", ").orElse("") +
            optionalTripId().map(f -> "tripId=" + f + ", ").orElse("") +
            optionalRouteId().map(f -> "routeId=" + f + ", ").orElse("") +
            optionalSeatId().map(f -> "seatId=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalQrCodeImgId().map(f -> "qrCodeImgId=" + f + ", ").orElse("") +
            optionalOriginalTicketId().map(f -> "originalTicketId=" + f + ", ").orElse("") +
            optionalExchangedTicketId().map(f -> "exchangedTicketId=" + f + ", ").orElse("") +
            optionalBookingId().map(f -> "bookingId=" + f + ", ").orElse("") +
            optionalExchangedFromId().map(f -> "exchangedFromId=" + f + ", ").orElse("") +
            optionalExchangedToId().map(f -> "exchangedToId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
