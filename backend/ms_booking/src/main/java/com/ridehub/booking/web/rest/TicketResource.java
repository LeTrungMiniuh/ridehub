package com.ridehub.booking.web.rest;

import com.ridehub.booking.repository.TicketRepository;
import com.ridehub.booking.service.TicketQueryService;
import com.ridehub.booking.service.TicketService;
import com.ridehub.booking.service.criteria.TicketCriteria;
import com.ridehub.booking.service.dto.TicketDTO;
import com.ridehub.booking.service.dto.request.TicketCancelRequestDTO;
import com.ridehub.booking.service.dto.request.TicketExchangeRequestDTO;
import com.ridehub.booking.service.dto.request.TicketRefundRequestDTO;
import com.ridehub.booking.service.dto.response.CheckinResponse;
import com.ridehub.booking.service.dto.response.TicketOperationResponseDTO;
import com.ridehub.booking.service.dto.response.TicketResponse;
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
 * REST controller for managing {@link com.ridehub.booking.domain.Ticket}.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketResource.class);
    private static final String ENTITY_NAME = "msBookingTicket";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final TicketQueryService ticketQueryService;

    public TicketResource(
            TicketService ticketService,
            TicketRepository ticketRepository,
            TicketQueryService ticketQueryService) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.ticketQueryService = ticketQueryService;
    }

    // -----------------------
    // JHipster CRUD endpoints
    // -----------------------

    @PostMapping("")
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketDTO ticketDTO) throws URISyntaxException {
        LOG.debug("REST request to save Ticket : {}", ticketDTO);
        if (ticketDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketDTO = ticketService.save(ticketDTO);
        return ResponseEntity
                .created(new URI("/api/tickets/" + ticketDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        ticketDTO.getId().toString()))
                .body(ticketDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody TicketDTO ticketDTO) throws URISyntaxException {
        LOG.debug("REST request to update Ticket : {}, {}", id, ticketDTO);
        if (ticketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!ticketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        ticketDTO = ticketService.update(ticketDTO);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        ticketDTO.getId().toString()))
                .body(ticketDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketDTO> partialUpdateTicket(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody TicketDTO ticketDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update Ticket partially : {}, {}", id, ticketDTO);
        if (ticketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!ticketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<TicketDTO> result = ticketService.partialUpdate(ticketDTO);
        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketDTO.getId().toString()));
    }

    /**
     * GET /api/tickets : criteria search (default).
     */
    @GetMapping("")
    public ResponseEntity<List<TicketDTO>> getAllTickets(TicketCriteria criteria) {
        LOG.debug("REST request to get Tickets by criteria: {}", criteria);
        List<TicketDTO> entityList = ticketQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTickets(TicketCriteria criteria) {
        LOG.debug("REST request to count Tickets by criteria: {}", criteria);
        return ResponseEntity.ok().body(ticketQueryService.countByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicket(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Ticket : {}", id);
        Optional<TicketDTO> ticketDTO = ticketService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Ticket : {}", id);
        ticketService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    // -------------------------------------------------------
    // Extra endpoints migrated from the old TicketController
    // -------------------------------------------------------

    /**
     * GET /api/tickets/by-booking-code : get tickets by booking code.
     */
    @GetMapping("/by-booking-code")
    public ResponseEntity<TicketResponse> getTicketsByBookingCode(@RequestParam String bookingCode) {
        LOG.debug("REST request to get Tickets by booking code: {}", bookingCode);
        List<TicketDTO> tickets = ticketService.findByBookingCode(bookingCode);
        List<String> qrCodes = tickets.stream()
                .map(TicketDTO::getQrCode)
                .filter(qr -> qr != null && !qr.isEmpty())
                .toList();
        return ResponseEntity.ok(new TicketResponse(tickets, qrCodes));
    }

    /**
     * POST /api/tickets/{code}/checkin : check in a ticket.
     */
    @PostMapping("/{code}/checkin")
    public ResponseEntity<CheckinResponse> checkinTicket(@PathVariable String code) {
        LOG.debug("REST request to check in Ticket with code: {}", code);

        TicketDTO ticket = ticketService.findByTicketCode(code)
                .orElseThrow(() -> new BadRequestAlertException("Ticket not found", ENTITY_NAME, "ticketnotfound"));
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            throw new BadRequestAlertException("Ticket already checked in", ENTITY_NAME, "alreadycheckedin");
        }

        ticketService.checkinTicket(code);
        return ResponseEntity.ok(new CheckinResponse(true));
    }

    // -------------------------------------------------------
    // Ticket Management Endpoints
    // -------------------------------------------------------

    /**
     * POST /api/tickets/{code}/cancel : cancel a ticket.
     */
    @PostMapping("/{code}/cancel")
    public ResponseEntity<TicketOperationResponseDTO> cancelTicket(
            @PathVariable String code,
            @Valid @RequestBody TicketCancelRequestDTO cancelRequest) {
        LOG.debug("REST request to cancel Ticket with code: {}", code);

        TicketOperationResponseDTO response = ticketService.cancelTicket(code, cancelRequest);
        if (Boolean.TRUE.equals(response.getSuccess())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/tickets/{code}/refund : request a refund for a ticket.
     */
    @PostMapping("/{code}/refund")
    public ResponseEntity<TicketOperationResponseDTO> refundTicket(
            @PathVariable String code,
            @Valid @RequestBody TicketRefundRequestDTO refundRequest) {
        LOG.debug("REST request to refund Ticket with code: {}", code);

        TicketOperationResponseDTO response = ticketService.refundTicket(code, refundRequest);
        if (Boolean.TRUE.equals(response.getSuccess())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/tickets/{code}/exchange : request an exchange for a ticket.
     */
    @PostMapping("/{code}/exchange")
    public ResponseEntity<TicketOperationResponseDTO> exchangeTicket(
            @PathVariable String code,
            @Valid @RequestBody TicketExchangeRequestDTO exchangeRequest) {
        LOG.debug("REST request to exchange Ticket with code: {}", code);

        TicketOperationResponseDTO response = ticketService.exchangeTicket(code, exchangeRequest);
        if (Boolean.TRUE.equals(response.getSuccess())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
