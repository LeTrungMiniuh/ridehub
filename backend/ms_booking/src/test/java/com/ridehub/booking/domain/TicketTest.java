package com.ridehub.booking.domain;

import static com.ridehub.booking.domain.BookingTestSamples.*;
import static com.ridehub.booking.domain.FileBookingTestSamples.*;
import static com.ridehub.booking.domain.TicketTestSamples.*;
import static com.ridehub.booking.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.booking.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ticket.class);
        Ticket ticket1 = getTicketSample1();
        Ticket ticket2 = new Ticket();
        assertThat(ticket1).isNotEqualTo(ticket2);

        ticket2.setId(ticket1.getId());
        assertThat(ticket1).isEqualTo(ticket2);

        ticket2 = getTicketSample2();
        assertThat(ticket1).isNotEqualTo(ticket2);
    }

    @Test
    void qrCodeImgTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        FileBooking fileBookingBack = getFileBookingRandomSampleGenerator();

        ticket.setQrCodeImg(fileBookingBack);
        assertThat(ticket.getQrCodeImg()).isEqualTo(fileBookingBack);

        ticket.qrCodeImg(null);
        assertThat(ticket.getQrCodeImg()).isNull();
    }

    @Test
    void originalTicketTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        ticket.setOriginalTicket(ticketBack);
        assertThat(ticket.getOriginalTicket()).isEqualTo(ticketBack);

        ticket.originalTicket(null);
        assertThat(ticket.getOriginalTicket()).isNull();
    }

    @Test
    void exchangedTicketTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        ticket.setExchangedTicket(ticketBack);
        assertThat(ticket.getExchangedTicket()).isEqualTo(ticketBack);

        ticket.exchangedTicket(null);
        assertThat(ticket.getExchangedTicket()).isNull();
    }

    @Test
    void bookingTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Booking bookingBack = getBookingRandomSampleGenerator();

        ticket.setBooking(bookingBack);
        assertThat(ticket.getBooking()).isEqualTo(bookingBack);

        ticket.booking(null);
        assertThat(ticket.getBooking()).isNull();
    }

    @Test
    void exchangedFromTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        ticket.addExchangedFrom(ticketBack);
        assertThat(ticket.getExchangedFroms()).containsOnly(ticketBack);
        assertThat(ticketBack.getOriginalTicket()).isEqualTo(ticket);

        ticket.removeExchangedFrom(ticketBack);
        assertThat(ticket.getExchangedFroms()).doesNotContain(ticketBack);
        assertThat(ticketBack.getOriginalTicket()).isNull();

        ticket.exchangedFroms(new HashSet<>(Set.of(ticketBack)));
        assertThat(ticket.getExchangedFroms()).containsOnly(ticketBack);
        assertThat(ticketBack.getOriginalTicket()).isEqualTo(ticket);

        ticket.setExchangedFroms(new HashSet<>());
        assertThat(ticket.getExchangedFroms()).doesNotContain(ticketBack);
        assertThat(ticketBack.getOriginalTicket()).isNull();
    }

    @Test
    void exchangedToTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        ticket.addExchangedTo(ticketBack);
        assertThat(ticket.getExchangedTos()).containsOnly(ticketBack);
        assertThat(ticketBack.getExchangedTicket()).isEqualTo(ticket);

        ticket.removeExchangedTo(ticketBack);
        assertThat(ticket.getExchangedTos()).doesNotContain(ticketBack);
        assertThat(ticketBack.getExchangedTicket()).isNull();

        ticket.exchangedTos(new HashSet<>(Set.of(ticketBack)));
        assertThat(ticket.getExchangedTos()).containsOnly(ticketBack);
        assertThat(ticketBack.getExchangedTicket()).isEqualTo(ticket);

        ticket.setExchangedTos(new HashSet<>());
        assertThat(ticket.getExchangedTos()).doesNotContain(ticketBack);
        assertThat(ticketBack.getExchangedTicket()).isNull();
    }
}
