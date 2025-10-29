package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Schedule;
import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.service.dto.ScheduleDTO;
import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ScheduleTimeSlot} and its DTO {@link ScheduleTimeSlotDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleTimeSlotMapper extends EntityMapper<ScheduleTimeSlotDTO, ScheduleTimeSlot> {
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "scheduleId")
    ScheduleTimeSlotDTO toDto(ScheduleTimeSlot s);

    @Named("scheduleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ScheduleDTO toDtoScheduleId(Schedule schedule);
}
