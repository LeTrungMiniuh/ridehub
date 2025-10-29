package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Schedule;
import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.ScheduleDTO;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
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
    ScheduleOccasionDTO toDtoScheduleOccasionId(ScheduleOccasion scheduleOccasion);

    @Named("routeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RouteDTO toDtoRouteId(Route route);
}
