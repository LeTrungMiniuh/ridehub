package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ScheduleOccasion} and its DTO {@link ScheduleOccasionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleOccasionMapper extends EntityMapper<ScheduleOccasionDTO, ScheduleOccasion> {}
