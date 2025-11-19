package com.ridehub.user.web.rest;

import com.ridehub.user.repository.TripRecommendationRepository;
import com.ridehub.user.service.TripRecommendationQueryService;
import com.ridehub.user.service.TripRecommendationService;
import com.ridehub.user.service.criteria.TripRecommendationCriteria;
import com.ridehub.user.service.dto.TripRecommendationDTO;
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
 * REST controller for managing {@link com.ridehub.user.domain.TripRecommendation}.
 */
@RestController
@RequestMapping("/api/trip-recommendations")
public class TripRecommendationResource {

    private static final Logger LOG = LoggerFactory.getLogger(TripRecommendationResource.class);

    private static final String ENTITY_NAME = "msUserTripRecommendation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TripRecommendationService tripRecommendationService;

    private final TripRecommendationRepository tripRecommendationRepository;

    private final TripRecommendationQueryService tripRecommendationQueryService;

    public TripRecommendationResource(
        TripRecommendationService tripRecommendationService,
        TripRecommendationRepository tripRecommendationRepository,
        TripRecommendationQueryService tripRecommendationQueryService
    ) {
        this.tripRecommendationService = tripRecommendationService;
        this.tripRecommendationRepository = tripRecommendationRepository;
        this.tripRecommendationQueryService = tripRecommendationQueryService;
    }

    /**
     * {@code POST  /trip-recommendations} : Create a new tripRecommendation.
     *
     * @param tripRecommendationDTO the tripRecommendationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tripRecommendationDTO, or with status {@code 400 (Bad Request)} if the tripRecommendation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TripRecommendationDTO> createTripRecommendation(@Valid @RequestBody TripRecommendationDTO tripRecommendationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TripRecommendation : {}", tripRecommendationDTO);
        if (tripRecommendationDTO.getId() != null) {
            throw new BadRequestAlertException("A new tripRecommendation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tripRecommendationDTO = tripRecommendationService.save(tripRecommendationDTO);
        return ResponseEntity.created(new URI("/api/trip-recommendations/" + tripRecommendationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tripRecommendationDTO.getId().toString()))
            .body(tripRecommendationDTO);
    }

    /**
     * {@code PUT  /trip-recommendations/:id} : Updates an existing tripRecommendation.
     *
     * @param id the id of the tripRecommendationDTO to save.
     * @param tripRecommendationDTO the tripRecommendationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tripRecommendationDTO,
     * or with status {@code 400 (Bad Request)} if the tripRecommendationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tripRecommendationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TripRecommendationDTO> updateTripRecommendation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TripRecommendationDTO tripRecommendationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TripRecommendation : {}, {}", id, tripRecommendationDTO);
        if (tripRecommendationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tripRecommendationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tripRecommendationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tripRecommendationDTO = tripRecommendationService.update(tripRecommendationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tripRecommendationDTO.getId().toString()))
            .body(tripRecommendationDTO);
    }

    /**
     * {@code PATCH  /trip-recommendations/:id} : Partial updates given fields of an existing tripRecommendation, field will ignore if it is null
     *
     * @param id the id of the tripRecommendationDTO to save.
     * @param tripRecommendationDTO the tripRecommendationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tripRecommendationDTO,
     * or with status {@code 400 (Bad Request)} if the tripRecommendationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tripRecommendationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tripRecommendationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TripRecommendationDTO> partialUpdateTripRecommendation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TripRecommendationDTO tripRecommendationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TripRecommendation partially : {}, {}", id, tripRecommendationDTO);
        if (tripRecommendationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tripRecommendationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tripRecommendationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TripRecommendationDTO> result = tripRecommendationService.partialUpdate(tripRecommendationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tripRecommendationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /trip-recommendations} : get all the tripRecommendations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tripRecommendations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TripRecommendationDTO>> getAllTripRecommendations(
        TripRecommendationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TripRecommendations by criteria: {}", criteria);

        Page<TripRecommendationDTO> page = tripRecommendationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /trip-recommendations/count} : count all the tripRecommendations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTripRecommendations(TripRecommendationCriteria criteria) {
        LOG.debug("REST request to count TripRecommendations by criteria: {}", criteria);
        return ResponseEntity.ok().body(tripRecommendationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /trip-recommendations/:id} : get the "id" tripRecommendation.
     *
     * @param id the id of the tripRecommendationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tripRecommendationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TripRecommendationDTO> getTripRecommendation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TripRecommendation : {}", id);
        Optional<TripRecommendationDTO> tripRecommendationDTO = tripRecommendationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tripRecommendationDTO);
    }

    /**
     * {@code DELETE  /trip-recommendations/:id} : delete the "id" tripRecommendation.
     *
     * @param id the id of the tripRecommendationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTripRecommendation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TripRecommendation : {}", id);
        tripRecommendationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
