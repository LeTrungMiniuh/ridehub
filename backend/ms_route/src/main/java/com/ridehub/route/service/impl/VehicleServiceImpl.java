package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Vehicle;
import com.ridehub.route.domain.enumeration.VehicleType;
import com.ridehub.route.repository.VehicleRepository;
import com.ridehub.route.service.VehicleService;
import com.ridehub.route.service.dto.VehicleDTO;
import com.ridehub.route.service.dto.VehicleTypeFactorUpdateDTO;
import com.ridehub.route.service.mapper.VehicleMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Vehicle}.
 */
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public VehicleDTO save(VehicleDTO vehicleDTO) {
        LOG.debug("Request to save Vehicle : {}", vehicleDTO);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    public VehicleDTO update(VehicleDTO vehicleDTO) {
        LOG.debug("Request to update Vehicle : {}", vehicleDTO);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    public Optional<VehicleDTO> partialUpdate(VehicleDTO vehicleDTO) {
        LOG.debug("Request to partially update Vehicle : {}", vehicleDTO);

        return vehicleRepository
            .findById(vehicleDTO.getId())
            .map(existingVehicle -> {
                vehicleMapper.partialUpdate(existingVehicle, vehicleDTO);

                return existingVehicle;
            })
            .map(vehicleRepository::save)
            .map(vehicleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleDTO> findOne(Long id) {
        LOG.debug("Request to get Vehicle : {}", id);
        return vehicleRepository.findById(id).map(vehicleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Vehicle : {}", id);
        vehicleRepository.deleteById(id);
    }

    @Override
    public int updateTypeFactorByType(VehicleTypeFactorUpdateDTO updateDTO) {
        LOG.debug("Request to update typeFactor for all vehicles of type : {}", updateDTO.getType());
        return vehicleRepository.updateTypeFactorByType(updateDTO.getType(), updateDTO.getTypeFactor());
    }
}
