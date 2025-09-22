package com.ridehub.booking.web.rest;

import com.ridehub.booking.repository.PaymentWebhookLogRepository;
import com.ridehub.booking.service.PaymentWebhookLogQueryService;
import com.ridehub.booking.service.PaymentWebhookLogService;
import com.ridehub.booking.service.criteria.PaymentWebhookLogCriteria;
import com.ridehub.booking.service.dto.PaymentWebhookLogDTO;
import com.ridehub.booking.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.ridehub.booking.domain.PaymentWebhookLog}.
 */
@RestController
@RequestMapping("/api/payment-webhook-logs")
public class PaymentWebhookLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentWebhookLogResource.class);

    private static final String ENTITY_NAME = "msBookingPaymentWebhookLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentWebhookLogService paymentWebhookLogService;

    private final PaymentWebhookLogRepository paymentWebhookLogRepository;

    private final PaymentWebhookLogQueryService paymentWebhookLogQueryService;

    public PaymentWebhookLogResource(
        PaymentWebhookLogService paymentWebhookLogService,
        PaymentWebhookLogRepository paymentWebhookLogRepository,
        PaymentWebhookLogQueryService paymentWebhookLogQueryService
    ) {
        this.paymentWebhookLogService = paymentWebhookLogService;
        this.paymentWebhookLogRepository = paymentWebhookLogRepository;
        this.paymentWebhookLogQueryService = paymentWebhookLogQueryService;
    }

    /**
     * {@code POST  /payment-webhook-logs} : Create a new paymentWebhookLog.
     *
     * @param paymentWebhookLogDTO the paymentWebhookLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentWebhookLogDTO, or with status {@code 400 (Bad Request)} if the paymentWebhookLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PaymentWebhookLogDTO> createPaymentWebhookLog(@Valid @RequestBody PaymentWebhookLogDTO paymentWebhookLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaymentWebhookLog : {}", paymentWebhookLogDTO);
        if (paymentWebhookLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentWebhookLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paymentWebhookLogDTO = paymentWebhookLogService.save(paymentWebhookLogDTO);
        return ResponseEntity.created(new URI("/api/payment-webhook-logs/" + paymentWebhookLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paymentWebhookLogDTO.getId().toString()))
            .body(paymentWebhookLogDTO);
    }

    /**
     * {@code PUT  /payment-webhook-logs/:id} : Updates an existing paymentWebhookLog.
     *
     * @param id the id of the paymentWebhookLogDTO to save.
     * @param paymentWebhookLogDTO the paymentWebhookLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentWebhookLogDTO,
     * or with status {@code 400 (Bad Request)} if the paymentWebhookLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentWebhookLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentWebhookLogDTO> updatePaymentWebhookLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentWebhookLogDTO paymentWebhookLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaymentWebhookLog : {}, {}", id, paymentWebhookLogDTO);
        if (paymentWebhookLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentWebhookLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentWebhookLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paymentWebhookLogDTO = paymentWebhookLogService.update(paymentWebhookLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentWebhookLogDTO.getId().toString()))
            .body(paymentWebhookLogDTO);
    }

    /**
     * {@code PATCH  /payment-webhook-logs/:id} : Partial updates given fields of an existing paymentWebhookLog, field will ignore if it is null
     *
     * @param id the id of the paymentWebhookLogDTO to save.
     * @param paymentWebhookLogDTO the paymentWebhookLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentWebhookLogDTO,
     * or with status {@code 400 (Bad Request)} if the paymentWebhookLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentWebhookLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentWebhookLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentWebhookLogDTO> partialUpdatePaymentWebhookLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentWebhookLogDTO paymentWebhookLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaymentWebhookLog partially : {}, {}", id, paymentWebhookLogDTO);
        if (paymentWebhookLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentWebhookLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentWebhookLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentWebhookLogDTO> result = paymentWebhookLogService.partialUpdate(paymentWebhookLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentWebhookLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payment-webhook-logs} : get all the paymentWebhookLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentWebhookLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PaymentWebhookLogDTO>> getAllPaymentWebhookLogs(PaymentWebhookLogCriteria criteria) {
        LOG.debug("REST request to get PaymentWebhookLogs by criteria: {}", criteria);

        List<PaymentWebhookLogDTO> entityList = paymentWebhookLogQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /payment-webhook-logs/count} : count all the paymentWebhookLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPaymentWebhookLogs(PaymentWebhookLogCriteria criteria) {
        LOG.debug("REST request to count PaymentWebhookLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(paymentWebhookLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /payment-webhook-logs/:id} : get the "id" paymentWebhookLog.
     *
     * @param id the id of the paymentWebhookLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentWebhookLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentWebhookLogDTO> getPaymentWebhookLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PaymentWebhookLog : {}", id);
        Optional<PaymentWebhookLogDTO> paymentWebhookLogDTO = paymentWebhookLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentWebhookLogDTO);
    }

    /**
     * {@code DELETE  /payment-webhook-logs/:id} : delete the "id" paymentWebhookLog.
     *
     * @param id the id of the paymentWebhookLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentWebhookLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PaymentWebhookLog : {}", id);
        paymentWebhookLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
