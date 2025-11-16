package com.ridehub.route.service.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public class ChatbotTripSearchRequest {
    private String origin; // "Hà Nội" or "01"
    private String destination; // "Đà Nẵng" or "48"

    private LocalDate date;

    private LocalTime departureTimeFrom;
    private LocalTime departureTimeTo;
    private LocalTime arrivalTimeFrom;
    private LocalTime arrivalTimeTo;

    public LocalTime getDepartureTimeFrom() {
        return departureTimeFrom;
    }

    public void setDepartureTimeFrom(LocalTime departureTimeFrom) {
        this.departureTimeFrom = departureTimeFrom;
    }

    public LocalTime getDepartureTimeTo() {
        return departureTimeTo;
    }

    public void setDepartureTimeTo(LocalTime departureTimeTo) {
        this.departureTimeTo = departureTimeTo;
    }

    public LocalTime getArrivalTimeFrom() {
        return arrivalTimeFrom;
    }

    public void setArrivalTimeFrom(LocalTime arrivalTimeFrom) {
        this.arrivalTimeFrom = arrivalTimeFrom;
    }

    public LocalTime getArrivalTimeTo() {
        return arrivalTimeTo;
    }

    public void setArrivalTimeTo(LocalTime arrivalTimeTo) {
        this.arrivalTimeTo = arrivalTimeTo;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
