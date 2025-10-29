package com.ridehub.route.domain;

import static com.ridehub.route.domain.AttendantTestSamples.*;
import static com.ridehub.route.domain.DriverTestSamples.*;
import static com.ridehub.route.domain.RouteTestSamples.*;
import static com.ridehub.route.domain.ScheduleTimeSlotTestSamples.*;
import static com.ridehub.route.domain.TripTestSamples.*;
import static com.ridehub.route.domain.VehicleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trip.class);
        Trip trip1 = getTripSample1();
        Trip trip2 = new Trip();
        assertThat(trip1).isNotEqualTo(trip2);

        trip2.setId(trip1.getId());
        assertThat(trip1).isEqualTo(trip2);

        trip2 = getTripSample2();
        assertThat(trip1).isNotEqualTo(trip2);
    }

    @Test
    void routeTest() {
        Trip trip = getTripRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        trip.setRoute(routeBack);
        assertThat(trip.getRoute()).isEqualTo(routeBack);

        trip.route(null);
        assertThat(trip.getRoute()).isNull();
    }

    @Test
    void vehicleTest() {
        Trip trip = getTripRandomSampleGenerator();
        Vehicle vehicleBack = getVehicleRandomSampleGenerator();

        trip.setVehicle(vehicleBack);
        assertThat(trip.getVehicle()).isEqualTo(vehicleBack);

        trip.vehicle(null);
        assertThat(trip.getVehicle()).isNull();
    }

    @Test
    void slotTest() {
        Trip trip = getTripRandomSampleGenerator();
        ScheduleTimeSlot scheduleTimeSlotBack = getScheduleTimeSlotRandomSampleGenerator();

        trip.setSlot(scheduleTimeSlotBack);
        assertThat(trip.getSlot()).isEqualTo(scheduleTimeSlotBack);

        trip.slot(null);
        assertThat(trip.getSlot()).isNull();
    }

    @Test
    void driverTest() {
        Trip trip = getTripRandomSampleGenerator();
        Driver driverBack = getDriverRandomSampleGenerator();

        trip.setDriver(driverBack);
        assertThat(trip.getDriver()).isEqualTo(driverBack);

        trip.driver(null);
        assertThat(trip.getDriver()).isNull();
    }

    @Test
    void attendantTest() {
        Trip trip = getTripRandomSampleGenerator();
        Attendant attendantBack = getAttendantRandomSampleGenerator();

        trip.setAttendant(attendantBack);
        assertThat(trip.getAttendant()).isEqualTo(attendantBack);

        trip.attendant(null);
        assertThat(trip.getAttendant()).isNull();
    }
}
