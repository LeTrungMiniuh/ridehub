package com.ridehub.route.web.rest;

import com.ridehub.route.repository.PricingTemplateRepository;
import com.ridehub.route.service.PricingTemplateQueryService;
import com.ridehub.route.service.PricingTemplateService;
import com.ridehub.route.service.TripPricingService;
import com.ridehub.route.service.criteria.PricingTemplateCriteria;
import com.ridehub.route.service.dto.PricingTemplateDTO;
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
 * REST controller for managing
 * {@link com.ridehub.route.domain.PricingTemplate}.
 */
@RestController
@RequestMapping("/api/pricing-templates")
public class PricingTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(PricingTemplateResource.class);

    private static final String ENTITY_NAME = "msRoutePricingTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PricingTemplateService pricingTemplateService;

    private final PricingTemplateRepository pricingTemplateRepository;

    private final PricingTemplateQueryService pricingTemplateQueryService;

    private final TripPricingService tripPricingService;

    public PricingTemplateResource(
            PricingTemplateService pricingTemplateService,
            PricingTemplateRepository pricingTemplateRepository,
            PricingTemplateQueryService pricingTemplateQueryService,
            TripPricingService tripPricingService) {
        this.pricingTemplateService = pricingTemplateService;
        this.pricingTemplateRepository = pricingTemplateRepository;
        this.pricingTemplateQueryService = pricingTemplateQueryService;
        this.tripPricingService = tripPricingService;
    }

    /**
     * {@code POST  /pricing-templates} : Create a new pricingTemplate.
     *
     * @param pricingTemplateDTO the pricingTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new pricingTemplateDTO, or with status
     *         {@code 400 (Bad Request)} if the pricingTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PricingTemplateDTO> createPricingTemplate(
            @Valid @RequestBody PricingTemplateDTO pricingTemplateDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save PricingTemplate : {}", pricingTemplateDTO);
        if (pricingTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new pricingTemplate cannot already have an ID", ENTITY_NAME,
                    "idexists");
        }
        pricingTemplateDTO = pricingTemplateService.save(pricingTemplateDTO);
        return ResponseEntity.created(new URI("/api/pricing-templates/" + pricingTemplateDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        pricingTemplateDTO.getId().toString()))
                .body(pricingTemplateDTO);
    }

    /**
     * {@code PUT  /pricing-templates/:id} : Updates an existing pricingTemplate.
     *
     * @param id                 the id of the pricingTemplateDTO to save.
     * @param pricingTemplateDTO the pricingTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated pricingTemplateDTO,
     *         or with status {@code 400 (Bad Request)} if the pricingTemplateDTO is
     *         not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         pricingTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PricingTemplateDTO> updatePricingTemplate(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody PricingTemplateDTO pricingTemplateDTO) throws URISyntaxException {
        LOG.debug("REST request to update PricingTemplate : {}, {}", id, pricingTemplateDTO);
        if (pricingTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pricingTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pricingTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pricingTemplateDTO = pricingTemplateService.update(pricingTemplateDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        pricingTemplateDTO.getId().toString()))
                .body(pricingTemplateDTO);
    }

    /**
     * {@code PATCH  /pricing-templates/:id} : Partial updates given fields of an
     * existing pricingTemplate, field will ignore if it is null
     *
     * @param id                 the id of the pricingTemplateDTO to save.
     * @param pricingTemplateDTO the pricingTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated pricingTemplateDTO,
     *         or with status {@code 400 (Bad Request)} if the pricingTemplateDTO is
     *         not valid,
     *         or with status {@code 404 (Not Found)} if the pricingTemplateDTO is
     *         not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         pricingTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PricingTemplateDTO> partialUpdatePricingTemplate(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody PricingTemplateDTO pricingTemplateDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update PricingTemplate partially : {}, {}", id, pricingTemplateDTO);
        if (pricingTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pricingTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pricingTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PricingTemplateDTO> result = pricingTemplateService.partialUpdate(pricingTemplateDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        pricingTemplateDTO.getId().toString()));
    }

    /**
     * {@code GET  /pricing-templates} : get all the pricingTemplates.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of pricingTemplates in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PricingTemplateDTO>> getAllPricingTemplates(
            PricingTemplateCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get PricingTemplates by criteria: {}", criteria);

        Page<PricingTemplateDTO> page = pricingTemplateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pricing-templates/count} : count all the pricingTemplates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPricingTemplates(PricingTemplateCriteria criteria) {
        LOG.debug("REST request to count PricingTemplates by criteria: {}", criteria);
        return ResponseEntity.ok().body(pricingTemplateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /pricing-templates/:id} : get the "id" pricingTemplate.
     *
     * @param id the id of the pricingTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the pricingTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PricingTemplateDTO> getPricingTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PricingTemplate : {}", id);
        Optional<PricingTemplateDTO> pricingTemplateDTO = pricingTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pricingTemplateDTO);
    }

    /**
     * {@code GET /pricing-templates/trip/{tripId}/seat/{seatId}} : get pricing
     * template by trip ID and seat ID.
     * If not exist, calculate using TripPricingService.
     *
     * @param tripId the ID of the trip.
     * @param seatId the ID of the seat.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the pricingTemplateDTO,
     *         or with status {@code 404 (Not Found)} if trip or seat not found.
     */
    @GetMapping("/trip/{tripId}/seat/{seatId}")
    public ResponseEntity<PricingTemplateDTO> getPricingTemplateByTripAndSeat(
            @PathVariable("tripId") Long tripId,
            @PathVariable("seatId") Long seatId) {
        LOG.debug("REST request to get PricingTemplate by tripId : {} and seatId : {}", tripId, seatId);

        PricingTemplateDTO pricingTemplate = tripPricingService.getPricingTemplateByTripAndSeat(tripId, seatId);

        return ResponseEntity.ok(pricingTemplate);
    }

    /**
     * {@code DELETE  /pricing-templates/:id} : delete the "id" pricingTemplate.
     *
     * @param id the id of the pricingTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricingTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PricingTemplate : {}", id);
        pricingTemplateService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
