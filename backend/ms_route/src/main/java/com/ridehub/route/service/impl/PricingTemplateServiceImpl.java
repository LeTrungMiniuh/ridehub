package com.ridehub.route.service.impl;

import com.ridehub.route.domain.PricingTemplate;
import com.ridehub.route.repository.PricingTemplateRepository;
import com.ridehub.route.service.PricingTemplateService;
import com.ridehub.route.service.dto.PricingTemplateDTO;
import com.ridehub.route.service.mapper.PricingTemplateMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.PricingTemplate}.
 */
@Service
@Transactional
public class PricingTemplateServiceImpl implements PricingTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(PricingTemplateServiceImpl.class);

    private final PricingTemplateRepository pricingTemplateRepository;

    private final PricingTemplateMapper pricingTemplateMapper;

    public PricingTemplateServiceImpl(PricingTemplateRepository pricingTemplateRepository, PricingTemplateMapper pricingTemplateMapper) {
        this.pricingTemplateRepository = pricingTemplateRepository;
        this.pricingTemplateMapper = pricingTemplateMapper;
    }

    @Override
    public PricingTemplateDTO save(PricingTemplateDTO pricingTemplateDTO) {
        LOG.debug("Request to save PricingTemplate : {}", pricingTemplateDTO);
        PricingTemplate pricingTemplate = pricingTemplateMapper.toEntity(pricingTemplateDTO);
        pricingTemplate = pricingTemplateRepository.save(pricingTemplate);
        return pricingTemplateMapper.toDto(pricingTemplate);
    }

    @Override
    public PricingTemplateDTO update(PricingTemplateDTO pricingTemplateDTO) {
        LOG.debug("Request to update PricingTemplate : {}", pricingTemplateDTO);
        PricingTemplate pricingTemplate = pricingTemplateMapper.toEntity(pricingTemplateDTO);
        pricingTemplate = pricingTemplateRepository.save(pricingTemplate);
        return pricingTemplateMapper.toDto(pricingTemplate);
    }

    @Override
    public Optional<PricingTemplateDTO> partialUpdate(PricingTemplateDTO pricingTemplateDTO) {
        LOG.debug("Request to partially update PricingTemplate : {}", pricingTemplateDTO);

        return pricingTemplateRepository
            .findById(pricingTemplateDTO.getId())
            .map(existingPricingTemplate -> {
                pricingTemplateMapper.partialUpdate(existingPricingTemplate, pricingTemplateDTO);

                return existingPricingTemplate;
            })
            .map(pricingTemplateRepository::save)
            .map(pricingTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PricingTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get PricingTemplate : {}", id);
        return pricingTemplateRepository.findById(id).map(pricingTemplateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PricingTemplate : {}", id);
        pricingTemplateRepository.deleteById(id);
    }
}
