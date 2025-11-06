package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Address;
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Schedule;
import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.domain.Station;
import com.ridehub.route.service.dto.AddressDTO;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.ScheduleDTO;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import com.ridehub.route.service.dto.StationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {
    @Mapping(target = "occasionRule", source = "occasionRule", qualifiedByName = "scheduleOccasionId")
    @Mapping(target = "route", source = "route", qualifiedByName = "routeId")
    ScheduleDTO toDto(Schedule s);

    @Named("scheduleOccasionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "occasion", source = "occasion")
    @Mapping(target = "occasionFactor", source = "occasionFactor")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "deletedBy", source = "deletedBy")
    ScheduleOccasionDTO toDtoScheduleOccasionId(ScheduleOccasion scheduleOccasion);

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
}
