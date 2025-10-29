package com.ridehub.route.web.rest;

import com.ridehub.route.repository.ScheduleOccasionRepository;
import com.ridehub.route.service.ScheduleOccasionQueryService;
import com.ridehub.route.service.ScheduleOccasionService;
import com.ridehub.route.service.criteria.ScheduleOccasionCriteria;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.route.domain.ScheduleOccasion}.
 */
@RestController
@RequestMapping("/api/schedule-occasions")
public class ScheduleOccasionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleOccasionResource.class);

    private static final String ENTITY_NAME = "msRouteScheduleOccasion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScheduleOccasionService scheduleOccasionService;

    private final ScheduleOccasionRepository scheduleOccasionRepository;

    private final ScheduleOccasionQueryService scheduleOccasionQueryService;

    public ScheduleOccasionResource(
        ScheduleOccasionService scheduleOccasionService,
        ScheduleOccasionRepository scheduleOccasionRepository,
        ScheduleOccasionQueryService scheduleOccasionQueryService
    ) {
        this.scheduleOccasionService = scheduleOccasionService;
        this.scheduleOccasionRepository = scheduleOccasionRepository;
        this.scheduleOccasionQueryService = scheduleOccasionQueryService;
    }

    /**
     * {@code POST  /schedule-occasions} : Create a new scheduleOccasion.
     *
     * @param scheduleOccasionDTO the scheduleOccasionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleOccasionDTO, or with status {@code 400 (Bad Request)} if the scheduleOccasion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScheduleOccasionDTO> createScheduleOccasion(@Valid @RequestBody ScheduleOccasionDTO scheduleOccasionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ScheduleOccasion : {}", scheduleOccasionDTO);
        if (scheduleOccasionDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheduleOccasion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scheduleOccasionDTO = scheduleOccasionService.save(scheduleOccasionDTO);
        return ResponseEntity.created(new URI("/api/schedule-occasions/" + scheduleOccasionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scheduleOccasionDTO.getId().toString()))
            .body(scheduleOccasionDTO);
    }

    /**
     * {@code PUT  /schedule-occasions/:id} : Updates an existing scheduleOccasion.
     *
     * @param id the id of the scheduleOccasionDTO to save.
     * @param scheduleOccasionDTO the scheduleOccasionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleOccasionDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleOccasionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleOccasionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleOccasionDTO> updateScheduleOccasion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleOccasionDTO scheduleOccasionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ScheduleOccasion : {}, {}", id, scheduleOccasionDTO);
        if (scheduleOccasionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleOccasionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleOccasionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scheduleOccasionDTO = scheduleOccasionService.update(scheduleOccasionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduleOccasionDTO.getId().toString()))
            .body(scheduleOccasionDTO);
    }

    /**
     * {@code PATCH  /schedule-occasions/:id} : Partial updates given fields of an existing scheduleOccasion, field will ignore if it is null
     *
     * @param id the id of the scheduleOccasionDTO to save.
     * @param scheduleOccasionDTO the scheduleOccasionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleOccasionDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleOccasionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleOccasionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleOccasionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScheduleOccasionDTO> partialUpdateScheduleOccasion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleOccasionDTO scheduleOccasionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ScheduleOccasion partially : {}, {}", id, scheduleOccasionDTO);
        if (scheduleOccasionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleOccasionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleOccasionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScheduleOccasionDTO> result = scheduleOccasionService.partialUpdate(scheduleOccasionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduleOccasionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /schedule-occasions} : get all the scheduleOccasions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scheduleOccasions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ScheduleOccasionDTO>> getAllScheduleOccasions(ScheduleOccasionCriteria criteria) {
        LOG.debug("REST request to get ScheduleOccasions by criteria: {}", criteria);

        List<ScheduleOccasionDTO> entityList = scheduleOccasionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /schedule-occasions/count} : count all the scheduleOccasions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countScheduleOccasions(ScheduleOccasionCriteria criteria) {
        LOG.debug("REST request to count ScheduleOccasions by criteria: {}", criteria);
        return ResponseEntity.ok().body(scheduleOccasionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /schedule-occasions/:id} : get the "id" scheduleOccasion.
     *
     * @param id the id of the scheduleOccasionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleOccasionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleOccasionDTO> getScheduleOccasion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ScheduleOccasion : {}", id);
        Optional<ScheduleOccasionDTO> scheduleOccasionDTO = scheduleOccasionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduleOccasionDTO);
    }

    /**
     * {@code DELETE  /schedule-occasions/:id} : delete the "id" scheduleOccasion.
     *
     * @param id the id of the scheduleOccasionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleOccasion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ScheduleOccasion : {}", id);
        scheduleOccasionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
