package com.ridehub.user.service.mapper;

import com.ridehub.user.domain.UserStatistics;
import com.ridehub.user.service.dto.UserStatisticsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserStatistics} and its DTO {@link UserStatisticsDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserStatisticsMapper extends EntityMapper<UserStatisticsDTO, UserStatistics> {}
