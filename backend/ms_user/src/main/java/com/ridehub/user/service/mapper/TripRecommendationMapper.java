package com.ridehub.user.service.mapper;

import com.ridehub.user.domain.AppUser;
import com.ridehub.user.domain.TripRecommendation;
import com.ridehub.user.service.dto.AppUserDTO;
import com.ridehub.user.service.dto.TripRecommendationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TripRecommendation} and its DTO {@link TripRecommendationDTO}.
 */
@Mapper(componentModel = "spring")
public interface TripRecommendationMapper extends EntityMapper<TripRecommendationDTO, TripRecommendation> {
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserId")
    TripRecommendationDTO toDto(TripRecommendation s);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);
}
