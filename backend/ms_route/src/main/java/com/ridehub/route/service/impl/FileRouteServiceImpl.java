package com.ridehub.route.service.impl;

import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.repository.FileRouteRepository;
import com.ridehub.route.service.FileRouteService;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.mapper.FileRouteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.FileRoute}.
 */
@Service
@Transactional
public class FileRouteServiceImpl implements FileRouteService {

    private static final Logger LOG = LoggerFactory.getLogger(FileRouteServiceImpl.class);

    private final FileRouteRepository fileRouteRepository;

    private final FileRouteMapper fileRouteMapper;

    public FileRouteServiceImpl(FileRouteRepository fileRouteRepository, FileRouteMapper fileRouteMapper) {
        this.fileRouteRepository = fileRouteRepository;
        this.fileRouteMapper = fileRouteMapper;
    }

    @Override
    public FileRouteDTO save(FileRouteDTO fileRouteDTO) {
        LOG.debug("Request to save FileRoute : {}", fileRouteDTO);
        FileRoute fileRoute = fileRouteMapper.toEntity(fileRouteDTO);
        fileRoute = fileRouteRepository.save(fileRoute);
        return fileRouteMapper.toDto(fileRoute);
    }

    @Override
    public FileRouteDTO update(FileRouteDTO fileRouteDTO) {
        LOG.debug("Request to update FileRoute : {}", fileRouteDTO);
        FileRoute fileRoute = fileRouteMapper.toEntity(fileRouteDTO);
        fileRoute = fileRouteRepository.save(fileRoute);
        return fileRouteMapper.toDto(fileRoute);
    }

    @Override
    public Optional<FileRouteDTO> partialUpdate(FileRouteDTO fileRouteDTO) {
        LOG.debug("Request to partially update FileRoute : {}", fileRouteDTO);

        return fileRouteRepository
            .findById(fileRouteDTO.getId())
            .map(existingFileRoute -> {
                fileRouteMapper.partialUpdate(existingFileRoute, fileRouteDTO);

                return existingFileRoute;
            })
            .map(fileRouteRepository::save)
            .map(fileRouteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FileRouteDTO> findOne(Long id) {
        LOG.debug("Request to get FileRoute : {}", id);
        return fileRouteRepository.findById(id).map(fileRouteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FileRoute : {}", id);
        fileRouteRepository.deleteById(id);
    }
}
