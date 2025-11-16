package com.ridehub.booking.service;

import com.ridehub.booking.domain.*; // for static metamodels
import com.ridehub.booking.domain.Ticket;
import com.ridehub.booking.repository.TicketRepository;
import com.ridehub.booking.service.criteria.TicketCriteria;
import com.ridehub.booking.service.dto.TicketDTO;
import com.ridehub.booking.service.mapper.TicketMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Ticket} entities in the database.
 * The main input is a {@link TicketCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TicketDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketQueryService extends QueryService<Ticket> {

    private static final Logger LOG = LoggerFactory.getLogger(TicketQueryService.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    public TicketQueryService(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    /**
     * Return a {@link List} of {@link TicketDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> findByCriteria(TicketCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketMapper.toDto(ticketRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ticket> createSpecification(TicketCriteria criteria) {
        Specification<Ticket> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Ticket_.id),
                buildStringSpecification(criteria.getTicketCode(), Ticket_.ticketCode),
                buildRangeSpecification(criteria.getPrice(), Ticket_.price),
                buildRangeSpecification(criteria.getTimeFrom(), Ticket_.timeFrom),
                buildRangeSpecification(criteria.getTimeTo(), Ticket_.timeTo),
                buildSpecification(criteria.getCheckedIn(), Ticket_.checkedIn),
                buildSpecification(criteria.getStatus(), Ticket_.status),
                buildSpecification(criteria.getRefundStatus(), Ticket_.refundStatus),
                buildStringSpecification(criteria.getRefundReason(), Ticket_.refundReason),
                buildRangeSpecification(criteria.getRefundRequestedAt(), Ticket_.refundRequestedAt),
                buildRangeSpecification(criteria.getRefundCompletedAt(), Ticket_.refundCompletedAt),
                buildRangeSpecification(criteria.getRefundAmount(), Ticket_.refundAmount),
                buildStringSpecification(criteria.getRefundTransactionId(), Ticket_.refundTransactionId),
                buildRangeSpecification(criteria.getTripId(), Ticket_.tripId),
                buildRangeSpecification(criteria.getRouteId(), Ticket_.routeId),
                buildRangeSpecification(criteria.getSeatId(), Ticket_.seatId),
                buildRangeSpecification(criteria.getCreatedAt(), Ticket_.createdAt),
                buildSpecification(criteria.getIsDeleted(), Ticket_.isDeleted),
                buildSpecification(criteria.getDeletedBy(), Ticket_.deletedBy),
                buildSpecification(criteria.getOriginalTicketId(), root -> root.join(Ticket_.originalTicket, JoinType.LEFT).get(Ticket_.id)
                ),
                buildSpecification(criteria.getExchangedTicketId(), root ->
                    root.join(Ticket_.exchangedTicket, JoinType.LEFT).get(Ticket_.id)
                ),
                buildSpecification(criteria.getBookingId(), root -> root.join(Ticket_.booking, JoinType.LEFT).get(Booking_.id)),
                buildSpecification(criteria.getExchangedFromId(), root -> root.join(Ticket_.exchangedFroms, JoinType.LEFT).get(Ticket_.id)),
                buildSpecification(criteria.getExchangedToId(), root -> root.join(Ticket_.exchangedTos, JoinType.LEFT).get(Ticket_.id))
            );
        }
        return specification;
    }
}
