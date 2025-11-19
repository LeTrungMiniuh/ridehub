package com.ridehub.user.service.impl;

import com.ridehub.user.domain.TripStatistics;
import com.ridehub.user.repository.TripStatisticsRepository;
import com.ridehub.user.service.TripStatisticsService;
import com.ridehub.user.service.dto.TripStatisticsDTO;
import com.ridehub.user.service.mapper.TripStatisticsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.user.domain.TripStatistics}.
 */
@Service
@Transactional
public class TripStatisticsServiceImpl implements TripStatisticsService {

    private static final Logger LOG = LoggerFactory.getLogger(TripStatisticsServiceImpl.class);

    private final TripStatisticsRepository tripStatisticsRepository;

    private final TripStatisticsMapper tripStatisticsMapper;

    public TripStatisticsServiceImpl(TripStatisticsRepository tripStatisticsRepository, TripStatisticsMapper tripStatisticsMapper) {
        this.tripStatisticsRepository = tripStatisticsRepository;
        this.tripStatisticsMapper = tripStatisticsMapper;
    }

    @Override
    public TripStatisticsDTO save(TripStatisticsDTO tripStatisticsDTO) {
        LOG.debug("Request to save TripStatistics : {}", tripStatisticsDTO);
        TripStatistics tripStatistics = tripStatisticsMapper.toEntity(tripStatisticsDTO);
        tripStatistics = tripStatisticsRepository.save(tripStatistics);
        return tripStatisticsMapper.toDto(tripStatistics);
    }

    @Override
    public TripStatisticsDTO update(TripStatisticsDTO tripStatisticsDTO) {
        LOG.debug("Request to update TripStatistics : {}", tripStatisticsDTO);
        TripStatistics tripStatistics = tripStatisticsMapper.toEntity(tripStatisticsDTO);
        tripStatistics = tripStatisticsRepository.save(tripStatistics);
        return tripStatisticsMapper.toDto(tripStatistics);
    }

    @Override
    public Optional<TripStatisticsDTO> partialUpdate(TripStatisticsDTO tripStatisticsDTO) {
        LOG.debug("Request to partially update TripStatistics : {}", tripStatisticsDTO);

        return tripStatisticsRepository
            .findById(tripStatisticsDTO.getId())
            .map(existingTripStatistics -> {
                tripStatisticsMapper.partialUpdate(existingTripStatistics, tripStatisticsDTO);

                return existingTripStatistics;
            })
            .map(tripStatisticsRepository::save)
            .map(tripStatisticsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripStatisticsDTO> findOne(Long id) {
        LOG.debug("Request to get TripStatistics : {}", id);
        return tripStatisticsRepository.findById(id).map(tripStatisticsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TripStatistics : {}", id);
        tripStatisticsRepository.deleteById(id);
    }
}
