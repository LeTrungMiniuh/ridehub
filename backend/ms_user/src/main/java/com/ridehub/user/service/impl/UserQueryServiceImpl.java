package com.ridehub.user.service.impl;

import com.ridehub.user.domain.UserQuery;
import com.ridehub.user.repository.UserQueryRepository;
import com.ridehub.user.service.UserQueryService;
import com.ridehub.user.service.dto.UserQueryDTO;
import com.ridehub.user.service.mapper.UserQueryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.user.domain.UserQuery}.
 */
@Service
@Transactional
public class UserQueryServiceImpl implements UserQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(UserQueryServiceImpl.class);

    private final UserQueryRepository userQueryRepository;

    private final UserQueryMapper userQueryMapper;

    public UserQueryServiceImpl(UserQueryRepository userQueryRepository, UserQueryMapper userQueryMapper) {
        this.userQueryRepository = userQueryRepository;
        this.userQueryMapper = userQueryMapper;
    }

    @Override
    public UserQueryDTO save(UserQueryDTO userQueryDTO) {
        LOG.debug("Request to save UserQuery : {}", userQueryDTO);
        UserQuery userQuery = userQueryMapper.toEntity(userQueryDTO);
        userQuery = userQueryRepository.save(userQuery);
        return userQueryMapper.toDto(userQuery);
    }

    @Override
    public UserQueryDTO update(UserQueryDTO userQueryDTO) {
        LOG.debug("Request to update UserQuery : {}", userQueryDTO);
        UserQuery userQuery = userQueryMapper.toEntity(userQueryDTO);
        userQuery = userQueryRepository.save(userQuery);
        return userQueryMapper.toDto(userQuery);
    }

    @Override
    public Optional<UserQueryDTO> partialUpdate(UserQueryDTO userQueryDTO) {
        LOG.debug("Request to partially update UserQuery : {}", userQueryDTO);

        return userQueryRepository
            .findById(userQueryDTO.getId())
            .map(existingUserQuery -> {
                userQueryMapper.partialUpdate(existingUserQuery, userQueryDTO);

                return existingUserQuery;
            })
            .map(userQueryRepository::save)
            .map(userQueryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserQueryDTO> findOne(Long id) {
        LOG.debug("Request to get UserQuery : {}", id);
        return userQueryRepository.findById(id).map(userQueryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserQuery : {}", id);
        userQueryRepository.deleteById(id);
    }
}
