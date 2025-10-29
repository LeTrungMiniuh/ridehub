package com.ridehub.route.service.impl;

import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.repository.ScheduleOccasionRepository;
import com.ridehub.route.service.ScheduleOccasionService;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import com.ridehub.route.service.mapper.ScheduleOccasionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.ScheduleOccasion}.
 */
@Service
@Transactional
public class ScheduleOccasionServiceImpl implements ScheduleOccasionService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleOccasionServiceImpl.class);

    private final ScheduleOccasionRepository scheduleOccasionRepository;

    private final ScheduleOccasionMapper scheduleOccasionMapper;

    public ScheduleOccasionServiceImpl(
        ScheduleOccasionRepository scheduleOccasionRepository,
        ScheduleOccasionMapper scheduleOccasionMapper
    ) {
        this.scheduleOccasionRepository = scheduleOccasionRepository;
        this.scheduleOccasionMapper = scheduleOccasionMapper;
    }

    @Override
    public ScheduleOccasionDTO save(ScheduleOccasionDTO scheduleOccasionDTO) {
        LOG.debug("Request to save ScheduleOccasion : {}", scheduleOccasionDTO);
        ScheduleOccasion scheduleOccasion = scheduleOccasionMapper.toEntity(scheduleOccasionDTO);
        scheduleOccasion = scheduleOccasionRepository.save(scheduleOccasion);
        return scheduleOccasionMapper.toDto(scheduleOccasion);
    }

    @Override
    public ScheduleOccasionDTO update(ScheduleOccasionDTO scheduleOccasionDTO) {
        LOG.debug("Request to update ScheduleOccasion : {}", scheduleOccasionDTO);
        ScheduleOccasion scheduleOccasion = scheduleOccasionMapper.toEntity(scheduleOccasionDTO);
        scheduleOccasion = scheduleOccasionRepository.save(scheduleOccasion);
        return scheduleOccasionMapper.toDto(scheduleOccasion);
    }

    @Override
    public Optional<ScheduleOccasionDTO> partialUpdate(ScheduleOccasionDTO scheduleOccasionDTO) {
        LOG.debug("Request to partially update ScheduleOccasion : {}", scheduleOccasionDTO);

        return scheduleOccasionRepository
            .findById(scheduleOccasionDTO.getId())
            .map(existingScheduleOccasion -> {
                scheduleOccasionMapper.partialUpdate(existingScheduleOccasion, scheduleOccasionDTO);

                return existingScheduleOccasion;
            })
            .map(scheduleOccasionRepository::save)
            .map(scheduleOccasionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleOccasionDTO> findOne(Long id) {
        LOG.debug("Request to get ScheduleOccasion : {}", id);
        return scheduleOccasionRepository.findById(id).map(scheduleOccasionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ScheduleOccasion : {}", id);
        scheduleOccasionRepository.deleteById(id);
    }
}
