package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Floor;
import com.ridehub.route.domain.Seat;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.dto.SeatDTO;
import com.ridehub.route.service.dto.SeatMapDTO;
import org.mapstruct.*;

/**
 * Mapper for entity {@link Seat} and its DTO {@link SeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeatMapper extends EntityMapper<SeatDTO, Seat> {
    @Mapping(target = "floor", source = "floor", qualifiedByName = "floorWithPriceFactor")
    SeatDTO toDto(Seat s);

    @Named("floorWithPriceFactor")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "floorNo", source = "floorNo")
    @Mapping(target = "priceFactorFloor", source = "priceFactorFloor")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "deletedBy", source = "deletedBy")
    @Mapping(target = "seatMap", source = "seatMap", qualifiedByName = "seatMapId")
    FloorDTO toDtoFloorWithPriceFactor(Floor floor);

    @Named("seatMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeatMapDTO toDtoSeatMapId(com.ridehub.route.domain.SeatMap seatMap);
}
