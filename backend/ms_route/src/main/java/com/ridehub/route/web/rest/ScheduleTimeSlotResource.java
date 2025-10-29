package com.ridehub.route.web.rest;

import com.ridehub.route.repository.ScheduleTimeSlotRepository;
import com.ridehub.route.service.ScheduleTimeSlotQueryService;
import com.ridehub.route.service.ScheduleTimeSlotService;
import com.ridehub.route.service.criteria.ScheduleTimeSlotCriteria;
import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
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
 * REST controller for managing {@link com.ridehub.route.domain.ScheduleTimeSlot}.
 */
@RestController
@RequestMapping("/api/schedule-time-slots")
public class ScheduleTimeSlotResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTimeSlotResource.class);

    private static final String ENTITY_NAME = "msRouteScheduleTimeSlot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScheduleTimeSlotService scheduleTimeSlotService;

    private final ScheduleTimeSlotRepository scheduleTimeSlotRepository;

    private final ScheduleTimeSlotQueryService scheduleTimeSlotQueryService;

    public ScheduleTimeSlotResource(
        ScheduleTimeSlotService scheduleTimeSlotService,
        ScheduleTimeSlotRepository scheduleTimeSlotRepository,
        ScheduleTimeSlotQueryService scheduleTimeSlotQueryService
    ) {
        this.scheduleTimeSlotService = scheduleTimeSlotService;
        this.scheduleTimeSlotRepository = scheduleTimeSlotRepository;
        this.scheduleTimeSlotQueryService = scheduleTimeSlotQueryService;
    }

    /**
     * {@code POST  /schedule-time-slots} : Create a new scheduleTimeSlot.
     *
     * @param scheduleTimeSlotDTO the scheduleTimeSlotDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleTimeSlotDTO, or with status {@code 400 (Bad Request)} if the scheduleTimeSlot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScheduleTimeSlotDTO> createScheduleTimeSlot(@Valid @RequestBody ScheduleTimeSlotDTO scheduleTimeSlotDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ScheduleTimeSlot : {}", scheduleTimeSlotDTO);
        if (scheduleTimeSlotDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheduleTimeSlot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scheduleTimeSlotDTO = scheduleTimeSlotService.save(scheduleTimeSlotDTO);
        return ResponseEntity.created(new URI("/api/schedule-time-slots/" + scheduleTimeSlotDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scheduleTimeSlotDTO.getId().toString()))
            .body(scheduleTimeSlotDTO);
    }

    /**
     * {@code PUT  /schedule-time-slots/:id} : Updates an existing scheduleTimeSlot.
     *
     * @param id the id of the scheduleTimeSlotDTO to save.
     * @param scheduleTimeSlotDTO the scheduleTimeSlotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleTimeSlotDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleTimeSlotDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleTimeSlotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleTimeSlotDTO> updateScheduleTimeSlot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleTimeSlotDTO scheduleTimeSlotDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ScheduleTimeSlot : {}, {}", id, scheduleTimeSlotDTO);
        if (scheduleTimeSlotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleTimeSlotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleTimeSlotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scheduleTimeSlotDTO = scheduleTimeSlotService.update(scheduleTimeSlotDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduleTimeSlotDTO.getId().toString()))
            .body(scheduleTimeSlotDTO);
    }

    /**
     * {@code PATCH  /schedule-time-slots/:id} : Partial updates given fields of an existing scheduleTimeSlot, field will ignore if it is null
     *
     * @param id the id of the scheduleTimeSlotDTO to save.
     * @param scheduleTimeSlotDTO the scheduleTimeSlotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleTimeSlotDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleTimeSlotDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleTimeSlotDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleTimeSlotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScheduleTimeSlotDTO> partialUpdateScheduleTimeSlot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleTimeSlotDTO scheduleTimeSlotDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ScheduleTimeSlot partially : {}, {}", id, scheduleTimeSlotDTO);
        if (scheduleTimeSlotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleTimeSlotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduleTimeSlotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScheduleTimeSlotDTO> result = scheduleTimeSlotService.partialUpdate(scheduleTimeSlotDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduleTimeSlotDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /schedule-time-slots} : get all the scheduleTimeSlots.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scheduleTimeSlots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ScheduleTimeSlotDTO>> getAllScheduleTimeSlots(ScheduleTimeSlotCriteria criteria) {
        LOG.debug("REST request to get ScheduleTimeSlots by criteria: {}", criteria);

        List<ScheduleTimeSlotDTO> entityList = scheduleTimeSlotQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /schedule-time-slots/count} : count all the scheduleTimeSlots.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countScheduleTimeSlots(ScheduleTimeSlotCriteria criteria) {
        LOG.debug("REST request to count ScheduleTimeSlots by criteria: {}", criteria);
        return ResponseEntity.ok().body(scheduleTimeSlotQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /schedule-time-slots/:id} : get the "id" scheduleTimeSlot.
     *
     * @param id the id of the scheduleTimeSlotDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleTimeSlotDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleTimeSlotDTO> getScheduleTimeSlot(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ScheduleTimeSlot : {}", id);
        Optional<ScheduleTimeSlotDTO> scheduleTimeSlotDTO = scheduleTimeSlotService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduleTimeSlotDTO);
    }

    /**
     * {@code DELETE  /schedule-time-slots/:id} : delete the "id" scheduleTimeSlot.
     *
     * @param id the id of the scheduleTimeSlotDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleTimeSlot(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ScheduleTimeSlot : {}", id);
        scheduleTimeSlotService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
