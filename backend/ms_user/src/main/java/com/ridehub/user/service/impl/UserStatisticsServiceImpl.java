package com.ridehub.user.service.impl;

import com.ridehub.user.domain.UserStatistics;
import com.ridehub.user.repository.UserStatisticsRepository;
import com.ridehub.user.service.UserStatisticsService;
import com.ridehub.user.service.dto.UserStatisticsDTO;
import com.ridehub.user.service.mapper.UserStatisticsMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.user.domain.UserStatistics}.
 */
@Service
@Transactional
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);

    private final UserStatisticsRepository userStatisticsRepository;

    private final UserStatisticsMapper userStatisticsMapper;

    public UserStatisticsServiceImpl(UserStatisticsRepository userStatisticsRepository, UserStatisticsMapper userStatisticsMapper) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.userStatisticsMapper = userStatisticsMapper;
    }

    @Override
    public UserStatisticsDTO save(UserStatisticsDTO userStatisticsDTO) {
        LOG.debug("Request to save UserStatistics : {}", userStatisticsDTO);
        UserStatistics userStatistics = userStatisticsMapper.toEntity(userStatisticsDTO);
        userStatistics = userStatisticsRepository.save(userStatistics);
        return userStatisticsMapper.toDto(userStatistics);
    }

    @Override
    public UserStatisticsDTO update(UserStatisticsDTO userStatisticsDTO) {
        LOG.debug("Request to update UserStatistics : {}", userStatisticsDTO);
        UserStatistics userStatistics = userStatisticsMapper.toEntity(userStatisticsDTO);
        userStatistics = userStatisticsRepository.save(userStatistics);
        return userStatisticsMapper.toDto(userStatistics);
    }

    @Override
    public Optional<UserStatisticsDTO> partialUpdate(UserStatisticsDTO userStatisticsDTO) {
        LOG.debug("Request to partially update UserStatistics : {}", userStatisticsDTO);

        return userStatisticsRepository
            .findById(userStatisticsDTO.getId())
            .map(existingUserStatistics -> {
                userStatisticsMapper.partialUpdate(existingUserStatistics, userStatisticsDTO);

                return existingUserStatistics;
            })
            .map(userStatisticsRepository::save)
            .map(userStatisticsMapper::toDto);
    }

    /**
     *  Get all the userStatistics where User is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserStatisticsDTO> findAllWhereUserIsNull() {
        LOG.debug("Request to get all userStatistics where User is null");
        return StreamSupport.stream(userStatisticsRepository.findAll().spliterator(), false)
            .filter(userStatistics -> userStatistics.getUser() == null)
            .map(userStatisticsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserStatisticsDTO> findOne(Long id) {
        LOG.debug("Request to get UserStatistics : {}", id);
        return userStatisticsRepository.findById(id).map(userStatisticsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserStatistics : {}", id);
        userStatisticsRepository.deleteById(id);
    }
}
