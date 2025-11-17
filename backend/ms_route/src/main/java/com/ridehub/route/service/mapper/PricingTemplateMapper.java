package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.PricingTemplate;
import com.ridehub.route.domain.Route;
import com.ridehub.route.service.dto.PricingTemplateDTO;
import com.ridehub.route.service.dto.RouteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PricingTemplate} and its DTO {@link PricingTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface PricingTemplateMapper extends EntityMapper<PricingTemplateDTO, PricingTemplate> {
    @Mapping(target = "route", source = "route", qualifiedByName = "routeId")
    PricingTemplateDTO toDto(PricingTemplate s);

    @Named("routeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RouteDTO toDtoRouteId(Route route);
}
