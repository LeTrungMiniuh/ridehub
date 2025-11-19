package com.ridehub.user.service.impl;

import com.ridehub.user.domain.TripRecommendation;
import com.ridehub.user.repository.TripRecommendationRepository;
import com.ridehub.user.service.TripRecommendationService;
import com.ridehub.user.service.dto.TripRecommendationDTO;
import com.ridehub.user.service.mapper.TripRecommendationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.user.domain.TripRecommendation}.
 */
@Service
@Transactional
public class TripRecommendationServiceImpl implements TripRecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(TripRecommendationServiceImpl.class);

    private final TripRecommendationRepository tripRecommendationRepository;

    private final TripRecommendationMapper tripRecommendationMapper;

    public TripRecommendationServiceImpl(
        TripRecommendationRepository tripRecommendationRepository,
        TripRecommendationMapper tripRecommendationMapper
    ) {
        this.tripRecommendationRepository = tripRecommendationRepository;
        this.tripRecommendationMapper = tripRecommendationMapper;
    }

    @Override
    public TripRecommendationDTO save(TripRecommendationDTO tripRecommendationDTO) {
        LOG.debug("Request to save TripRecommendation : {}", tripRecommendationDTO);
        TripRecommendation tripRecommendation = tripRecommendationMapper.toEntity(tripRecommendationDTO);
        tripRecommendation = tripRecommendationRepository.save(tripRecommendation);
        return tripRecommendationMapper.toDto(tripRecommendation);
    }

    @Override
    public TripRecommendationDTO update(TripRecommendationDTO tripRecommendationDTO) {
        LOG.debug("Request to update TripRecommendation : {}", tripRecommendationDTO);
        TripRecommendation tripRecommendation = tripRecommendationMapper.toEntity(tripRecommendationDTO);
        tripRecommendation = tripRecommendationRepository.save(tripRecommendation);
        return tripRecommendationMapper.toDto(tripRecommendation);
    }

    @Override
    public Optional<TripRecommendationDTO> partialUpdate(TripRecommendationDTO tripRecommendationDTO) {
        LOG.debug("Request to partially update TripRecommendation : {}", tripRecommendationDTO);

        return tripRecommendationRepository
            .findById(tripRecommendationDTO.getId())
            .map(existingTripRecommendation -> {
                tripRecommendationMapper.partialUpdate(existingTripRecommendation, tripRecommendationDTO);

                return existingTripRecommendation;
            })
            .map(tripRecommendationRepository::save)
            .map(tripRecommendationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripRecommendationDTO> findOne(Long id) {
        LOG.debug("Request to get TripRecommendation : {}", id);
        return tripRecommendationRepository.findById(id).map(tripRecommendationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TripRecommendation : {}", id);
        tripRecommendationRepository.deleteById(id);
    }
}
