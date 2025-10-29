package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Schedule;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.ScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {
    @Mapping(target = "route", source = "route", qualifiedByName = "routeId")
    ScheduleDTO toDto(Schedule s);

    @Named("routeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RouteDTO toDtoRouteId(Route route);
}
