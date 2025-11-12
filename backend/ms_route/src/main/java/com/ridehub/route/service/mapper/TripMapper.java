package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Address;
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.domain.Station;
import com.ridehub.route.domain.Attendant;
import com.ridehub.route.domain.Driver;
import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.domain.Trip;
import com.ridehub.route.domain.Vehicle;

import com.ridehub.route.service.dto.AddressDTO;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.dto.SeatMapDTO;
import com.ridehub.route.service.dto.StationDTO;
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

    /*
     * ---------- Route (with origin/destination -> station -> address/fileRoute)
     * ----------
     */

    @Named("routeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "routeCode", source = "routeCode")
    @Mapping(target = "distanceKm", source = "distanceKm")
    @Mapping(target = "baseFare", source = "baseFare")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "deletedBy", source = "deletedBy")
    @Mapping(target = "origin", source = "origin", qualifiedByName = "stationId")
    @Mapping(target = "destination", source = "destination", qualifiedByName = "stationId")
    RouteDTO toDtoRouteId(Route route);

    @Named("stationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "deletedBy", source = "deletedBy")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    @Mapping(target = "stationImg", source = "stationImg", qualifiedByName = "fileRouteId")
    StationDTO toDtoStationId(Station station);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);

    @Named("fileRouteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileRouteDTO toDtoFileRouteId(FileRoute fileRoute);

    /*
     * --------------------------- Vehicle (with seatMap/image)
     * ---------------------------
     */

    @Named("vehicleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "typeFactor", source = "typeFactor")
    @Mapping(target = "plateNumber", source = "plateNumber")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "deletedBy", source = "deletedBy")
    @Mapping(target = "seatMap", source = "seatMap", qualifiedByName = "seatMapId")
    @Mapping(target = "vehicleImg", source = "vehicleImg", qualifiedByName = "fileRouteId")
    VehicleDTO toDtoVehicleId(Vehicle vehicle);

    @Named("seatMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeatMapDTO toDtoSeatMapId(SeatMap seatMap);

    /*
     * -------------------------- TimeSlot / Driver / Attendant
     * --------------------------
     */

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
