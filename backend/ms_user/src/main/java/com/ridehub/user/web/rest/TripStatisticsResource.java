package com.ridehub.user.web.rest;

import com.ridehub.user.repository.TripStatisticsRepository;
import com.ridehub.user.service.TripStatisticsQueryService;
import com.ridehub.user.service.TripStatisticsService;
import com.ridehub.user.service.criteria.TripStatisticsCriteria;
import com.ridehub.user.service.dto.TripStatisticsDTO;
import com.ridehub.user.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.user.domain.TripStatistics}.
 */
@RestController
@RequestMapping("/api/trip-statistics")
public class TripStatisticsResource {

    private static final Logger LOG = LoggerFactory.getLogger(TripStatisticsResource.class);

    private static final String ENTITY_NAME = "msUserTripStatistics";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TripStatisticsService tripStatisticsService;

    private final TripStatisticsRepository tripStatisticsRepository;

    private final TripStatisticsQueryService tripStatisticsQueryService;

    public TripStatisticsResource(
        TripStatisticsService tripStatisticsService,
        TripStatisticsRepository tripStatisticsRepository,
        TripStatisticsQueryService tripStatisticsQueryService
    ) {
        this.tripStatisticsService = tripStatisticsService;
        this.tripStatisticsRepository = tripStatisticsRepository;
        this.tripStatisticsQueryService = tripStatisticsQueryService;
    }

    /**
     * {@code POST  /trip-statistics} : Create a new tripStatistics.
     *
     * @param tripStatisticsDTO the tripStatisticsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tripStatisticsDTO, or with status {@code 400 (Bad Request)} if the tripStatistics has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TripStatisticsDTO> createTripStatistics(@Valid @RequestBody TripStatisticsDTO tripStatisticsDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TripStatistics : {}", tripStatisticsDTO);
        if (tripStatisticsDTO.getId() != null) {
            throw new BadRequestAlertException("A new tripStatistics cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tripStatisticsDTO = tripStatisticsService.save(tripStatisticsDTO);
        return ResponseEntity.created(new URI("/api/trip-statistics/" + tripStatisticsDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tripStatisticsDTO.getId().toString()))
            .body(tripStatisticsDTO);
    }

    /**
     * {@code PUT  /trip-statistics/:id} : Updates an existing tripStatistics.
     *
     * @param id the id of the tripStatisticsDTO to save.
     * @param tripStatisticsDTO the tripStatisticsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tripStatisticsDTO,
     * or with status {@code 400 (Bad Request)} if the tripStatisticsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tripStatisticsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TripStatisticsDTO> updateTripStatistics(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TripStatisticsDTO tripStatisticsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TripStatistics : {}, {}", id, tripStatisticsDTO);
        if (tripStatisticsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tripStatisticsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tripStatisticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tripStatisticsDTO = tripStatisticsService.update(tripStatisticsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tripStatisticsDTO.getId().toString()))
            .body(tripStatisticsDTO);
    }

    /**
     * {@code PATCH  /trip-statistics/:id} : Partial updates given fields of an existing tripStatistics, field will ignore if it is null
     *
     * @param id the id of the tripStatisticsDTO to save.
     * @param tripStatisticsDTO the tripStatisticsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tripStatisticsDTO,
     * or with status {@code 400 (Bad Request)} if the tripStatisticsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tripStatisticsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tripStatisticsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TripStatisticsDTO> partialUpdateTripStatistics(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TripStatisticsDTO tripStatisticsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TripStatistics partially : {}, {}", id, tripStatisticsDTO);
        if (tripStatisticsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tripStatisticsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tripStatisticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TripStatisticsDTO> result = tripStatisticsService.partialUpdate(tripStatisticsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tripStatisticsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /trip-statistics} : get all the tripStatistics.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tripStatistics in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TripStatisticsDTO>> getAllTripStatistics(
        TripStatisticsCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TripStatistics by criteria: {}", criteria);

        Page<TripStatisticsDTO> page = tripStatisticsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /trip-statistics/count} : count all the tripStatistics.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTripStatistics(TripStatisticsCriteria criteria) {
        LOG.debug("REST request to count TripStatistics by criteria: {}", criteria);
        return ResponseEntity.ok().body(tripStatisticsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /trip-statistics/:id} : get the "id" tripStatistics.
     *
     * @param id the id of the tripStatisticsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tripStatisticsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TripStatisticsDTO> getTripStatistics(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TripStatistics : {}", id);
        Optional<TripStatisticsDTO> tripStatisticsDTO = tripStatisticsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tripStatisticsDTO);
    }

    /**
     * {@code DELETE  /trip-statistics/:id} : delete the "id" tripStatistics.
     *
     * @param id the id of the tripStatisticsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTripStatistics(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TripStatistics : {}", id);
        tripStatisticsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
