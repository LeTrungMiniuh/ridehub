package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Attendant;
import com.ridehub.route.domain.Driver;
import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.domain.Trip;
import com.ridehub.route.domain.Vehicle;
import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.dto.DriverDTO;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
import com.ridehub.route.service.dto.TripDTO;
import com.ridehub.route.service.dto.VehicleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Trip} and its DTO {@link TripDTO}.
 */
@Mapper(componentModel = "spring")
public interface TripMapper extends EntityMapper<TripDTO, Trip> {
    @Mapping(target = "route", source = "route", qualifiedByName = "routeId")
    @Mapping(target = "vehicle", source = "vehicle", qualifiedByName = "vehicleId")
    @Mapping(target = "slot", source = "slot", qualifiedByName = "scheduleTimeSlotId")
    @Mapping(target = "driver", source = "driver", qualifiedByName = "driverId")
    @Mapping(target = "attendant", source = "attendant", qualifiedByName = "attendantId")
    TripDTO toDto(Trip s);

    @Named("routeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RouteDTO toDtoRouteId(Route route);

    @Named("vehicleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VehicleDTO toDtoVehicleId(Vehicle vehicle);

    @Named("scheduleTimeSlotId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ScheduleTimeSlotDTO toDtoScheduleTimeSlotId(ScheduleTimeSlot scheduleTimeSlot);

    @Named("driverId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DriverDTO toDtoDriverId(Driver driver);

    @Named("attendantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttendantDTO toDtoAttendantId(Attendant attendant);
}
