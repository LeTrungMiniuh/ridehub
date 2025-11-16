package com.ridehub.user.service.mapper;

import com.ridehub.user.domain.TripStatistics;
import com.ridehub.user.service.dto.TripStatisticsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TripStatistics} and its DTO {@link TripStatisticsDTO}.
 */
@Mapper(componentModel = "spring")
public interface TripStatisticsMapper extends EntityMapper<TripStatisticsDTO, TripStatistics> {}
