package com.ridehub.route.service.impl;

import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.repository.ScheduleTimeSlotRepository;
import com.ridehub.route.service.ScheduleTimeSlotService;
import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
import com.ridehub.route.service.mapper.ScheduleTimeSlotMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.ScheduleTimeSlot}.
 */
@Service
@Transactional
public class ScheduleTimeSlotServiceImpl implements ScheduleTimeSlotService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTimeSlotServiceImpl.class);

    private final ScheduleTimeSlotRepository scheduleTimeSlotRepository;

    private final ScheduleTimeSlotMapper scheduleTimeSlotMapper;

    public ScheduleTimeSlotServiceImpl(
        ScheduleTimeSlotRepository scheduleTimeSlotRepository,
        ScheduleTimeSlotMapper scheduleTimeSlotMapper
    ) {
        this.scheduleTimeSlotRepository = scheduleTimeSlotRepository;
        this.scheduleTimeSlotMapper = scheduleTimeSlotMapper;
    }

    @Override
    public ScheduleTimeSlotDTO save(ScheduleTimeSlotDTO scheduleTimeSlotDTO) {
        LOG.debug("Request to save ScheduleTimeSlot : {}", scheduleTimeSlotDTO);
        ScheduleTimeSlot scheduleTimeSlot = scheduleTimeSlotMapper.toEntity(scheduleTimeSlotDTO);
        scheduleTimeSlot = scheduleTimeSlotRepository.save(scheduleTimeSlot);
        return scheduleTimeSlotMapper.toDto(scheduleTimeSlot);
    }

    @Override
    public ScheduleTimeSlotDTO update(ScheduleTimeSlotDTO scheduleTimeSlotDTO) {
        LOG.debug("Request to update ScheduleTimeSlot : {}", scheduleTimeSlotDTO);
        ScheduleTimeSlot scheduleTimeSlot = scheduleTimeSlotMapper.toEntity(scheduleTimeSlotDTO);
        scheduleTimeSlot = scheduleTimeSlotRepository.save(scheduleTimeSlot);
        return scheduleTimeSlotMapper.toDto(scheduleTimeSlot);
    }

    @Override
    public Optional<ScheduleTimeSlotDTO> partialUpdate(ScheduleTimeSlotDTO scheduleTimeSlotDTO) {
        LOG.debug("Request to partially update ScheduleTimeSlot : {}", scheduleTimeSlotDTO);

        return scheduleTimeSlotRepository
            .findById(scheduleTimeSlotDTO.getId())
            .map(existingScheduleTimeSlot -> {
                scheduleTimeSlotMapper.partialUpdate(existingScheduleTimeSlot, scheduleTimeSlotDTO);

                return existingScheduleTimeSlot;
            })
            .map(scheduleTimeSlotRepository::save)
            .map(scheduleTimeSlotMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleTimeSlotDTO> findOne(Long id) {
        LOG.debug("Request to get ScheduleTimeSlot : {}", id);
        return scheduleTimeSlotRepository.findById(id).map(scheduleTimeSlotMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ScheduleTimeSlot : {}", id);
        scheduleTimeSlotRepository.deleteById(id);
    }
}
