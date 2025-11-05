package com.ridehub.booking.service.impl;

import com.ridehub.booking.domain.Ticket;
import com.ridehub.booking.domain.enumeration.AvroTicketStatus;
import com.ridehub.booking.domain.enumeration.ExchangeStatus;
import com.ridehub.booking.domain.enumeration.RefundStatus;
import com.ridehub.booking.repository.TicketRepository;
import com.ridehub.booking.service.TicketService;
import com.ridehub.booking.service.dto.TicketDTO;
import com.ridehub.booking.service.dto.request.TicketCancelRequestDTO;
import com.ridehub.booking.service.dto.request.TicketExchangeRequestDTO;
import com.ridehub.booking.service.dto.request.TicketRefundRequestDTO;
import com.ridehub.booking.service.dto.response.TicketOperationResponseDTO;
import com.ridehub.booking.service.mapper.TicketMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.booking.domain.Ticket}.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public TicketDTO save(TicketDTO ticketDTO) {
        LOG.debug("Request to save Ticket : {}", ticketDTO);
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket = ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public TicketDTO update(TicketDTO ticketDTO) {
        LOG.debug("Request to update Ticket : {}", ticketDTO);
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket = ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public Optional<TicketDTO> partialUpdate(TicketDTO ticketDTO) {
        LOG.debug("Request to partially update Ticket : {}", ticketDTO);

        return ticketRepository
            .findById(ticketDTO.getId())
            .map(existingTicket -> {
                ticketMapper.partialUpdate(existingTicket, ticketDTO);

                return existingTicket;
            })
            .map(ticketRepository::save)
            .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDTO> findOne(Long id) {
        LOG.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id).map(ticketMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Ticket : {}", id);
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> findByBookingCode(String bookingCode) {
        LOG.debug("Request to get Tickets by booking code : {}", bookingCode);
        return ticketRepository.findByBooking_BookingCode(bookingCode)
            .stream()
            .map(ticketMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDTO> findByTicketCode(String ticketCode) {
        LOG.debug("Request to get Ticket by ticket code : {}", ticketCode);
        return ticketRepository.findByTicketCode(ticketCode)
            .map(ticketMapper::toDto);
    }

    @Override
    public Optional<TicketDTO> checkinTicket(String ticketCode) {
        LOG.debug("Request to check in Ticket with code : {}", ticketCode);
        return ticketRepository.findByTicketCode(ticketCode)
            .map(ticket -> {
                ticket.setCheckedIn(true);
                return ticketRepository.save(ticket);
            })
            .map(ticketMapper::toDto);
    }

    @Override
    public TicketOperationResponseDTO cancelTicket(String ticketCode, TicketCancelRequestDTO cancelRequest) {
        LOG.debug("Request to cancel Ticket with code : {}", ticketCode);
        
        return ticketRepository.findByTicketCode(ticketCode)
            .map(ticket -> {
                // Validate ticket can be cancelled
                if (!canCancelTicket(ticket)) {
                    return new TicketOperationResponseDTO(
                        ticketMapper.toDto(ticket), 
                        "Ticket cannot be cancelled", 
                        false
                    );
                }
                
                // Update ticket status and details
                ticket.setStatus(AvroTicketStatus.CANCELLED);
                ticket.setUpdatedAt(Instant.now());
                
                Ticket savedTicket = ticketRepository.save(ticket);
                return new TicketOperationResponseDTO(
                    ticketMapper.toDto(savedTicket), 
                    "Ticket cancelled successfully", 
                    true
                );
            })
            .orElse(new TicketOperationResponseDTO(null, "Ticket not found", false));
    }

    @Override
    public TicketOperationResponseDTO refundTicket(String ticketCode, TicketRefundRequestDTO refundRequest) {
        LOG.debug("Request to refund Ticket with code : {}", ticketCode);
        
        return ticketRepository.findByTicketCode(ticketCode)
            .map(ticket -> {
                // Validate ticket can be refunded
                if (!canRefundTicket(ticket)) {
                    return new TicketOperationResponseDTO(
                        ticketMapper.toDto(ticket), 
                        "Ticket cannot be refunded", 
                        false
                    );
                }
                
                // Update ticket refund status and details
                ticket.setStatus(AvroTicketStatus.REFUND_REQUESTED);
                ticket.setRefundStatus(RefundStatus.REFUND_REQUESTED);
                ticket.setRefundReason(refundRequest.getReason());
                ticket.setRefundAmount(refundRequest.getRefundAmount());
                ticket.setRefundRequestedAt(Instant.now());
                ticket.setUpdatedAt(Instant.now());
                
                Ticket savedTicket = ticketRepository.save(ticket);
                return new TicketOperationResponseDTO(
                    ticketMapper.toDto(savedTicket), 
                    "Refund request submitted successfully", 
                    true
                );
            })
            .orElse(new TicketOperationResponseDTO(null, "Ticket not found", false));
    }

    @Override
    public TicketOperationResponseDTO exchangeTicket(String ticketCode, TicketExchangeRequestDTO exchangeRequest) {
        LOG.debug("Request to exchange Ticket with code : {}", ticketCode);
        
        return ticketRepository.findByTicketCode(ticketCode)
            .map(ticket -> {
                // Validate ticket can be exchanged
                if (!canExchangeTicket(ticket)) {
                    return new TicketOperationResponseDTO(
                        ticketMapper.toDto(ticket), 
                        "Ticket cannot be exchanged", 
                        false
                    );
                }
                
                // Update ticket exchange status and details
                ticket.setStatus(AvroTicketStatus.EXCHANGE_REQUESTED);
                ticket.setExchangeStatus(ExchangeStatus.EXCHANGE_REQUESTED);
                ticket.setExchangeReason(exchangeRequest.getReason());
                ticket.setExchangeRequestedAt(Instant.now());
                ticket.setUpdatedAt(Instant.now());
                
                // Store new trip/route/seat info for future processing
                // In a real implementation, you might create a new ticket or update existing one
                // For now, we'll just update the current ticket status
                
                Ticket savedTicket = ticketRepository.save(ticket);
                return new TicketOperationResponseDTO(
                    ticketMapper.toDto(savedTicket), 
                    "Exchange request submitted successfully", 
                    true
                );
            })
            .orElse(new TicketOperationResponseDTO(null, "Ticket not found", false));
    }

    /**
     * Validate if a ticket can be cancelled.
     */
    private boolean canCancelTicket(Ticket ticket) {
        // Ticket cannot be cancelled if already checked in
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            return false;
        }
        
        // Ticket cannot be cancelled if already cancelled, refunded, or exchanged
        AvroTicketStatus status = ticket.getStatus();
        if (status == AvroTicketStatus.CANCELLED || 
            status == AvroTicketStatus.REFUND_COMPLETED || 
            status == AvroTicketStatus.EXCHANGE_COMPLETED) {
            return false;
        }
        
        // Add more business rules as needed
        // For example: time-based restrictions, etc.
        
        return true;
    }

    /**
     * Validate if a ticket can be refunded.
     */
    private boolean canRefundTicket(Ticket ticket) {
        // Ticket cannot be refunded if already checked in
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            return false;
        }
        
        // Ticket cannot be refunded if already refunded or exchanged
        AvroTicketStatus status = ticket.getStatus();
        if (status == AvroTicketStatus.REFUND_COMPLETED || 
            status == AvroTicketStatus.REFUND_FAILED ||
            status == AvroTicketStatus.EXCHANGE_COMPLETED) {
            return false;
        }
        
        // Add more business rules as needed
        // For example: time-based restrictions, refund policies, etc.
        
        return true;
    }

    /**
     * Validate if a ticket can be exchanged.
     */
    private boolean canExchangeTicket(Ticket ticket) {
        // Ticket cannot be exchanged if already checked in
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            return false;
        }
        
        // Ticket cannot be exchanged if already exchanged, refunded, or cancelled
        AvroTicketStatus status = ticket.getStatus();
        if (status == AvroTicketStatus.EXCHANGE_COMPLETED || 
            status == AvroTicketStatus.EXCHANGE_REJECTED ||
            status == AvroTicketStatus.REFUND_COMPLETED ||
            status == AvroTicketStatus.CANCELLED) {
            return false;
        }
        
        // Add more business rules as needed
        // For example: time-based restrictions, exchange policies, etc.
        
        return true;
    }
}
