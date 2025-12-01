package com.ridehub.route.service.impl;

import com.ridehub.route.domain.SeatLock;
import com.ridehub.route.domain.Trip;
import com.ridehub.route.domain.enumeration.LockStatus;
import com.ridehub.route.repository.SeatLockRepository;
import com.ridehub.route.repository.TripRepository;
import com.ridehub.route.service.SeatLockQueryService;
import com.ridehub.route.service.SeatLockService;
import com.ridehub.route.service.criteria.SeatLockCriteria;
import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.dto.request.*;
import com.ridehub.route.service.dto.response.SeatHoldResponseDTO;
import com.ridehub.route.service.dto.response.SeatLockActionResponseDTO;
import com.ridehub.route.service.dto.response.SeatLockResponseDTO;
import com.ridehub.route.service.dto.response.SeatValidateLockResponseDTO;
import com.ridehub.route.service.mapper.SeatLockMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeatLockServiceImpl implements SeatLockService {

    private static final Logger LOG = LoggerFactory.getLogger(SeatLockServiceImpl.class);

    private final SeatLockRepository seatLockRepository;
    private final TripRepository tripRepository;
    private final SeatLockQueryService queryService;
    private final SeatLockMapper seatLockMapper;

    public SeatLockServiceImpl(
            SeatLockRepository seatLockRepository,
            TripRepository tripRepository,
            SeatLockQueryService queryService,
            SeatLockMapper seatLockMapper) {
        this.seatLockRepository = seatLockRepository;
        this.tripRepository = tripRepository;
        this.queryService = queryService;
        this.seatLockMapper = seatLockMapper;
    }

    private static String normSeat(String s) {
        return s == null ? null : s.trim().toUpperCase();
    }

    // Required interface methods from existing service
    @Override
    public SeatLockDTO save(SeatLockDTO seatLockDTO) {
        LOG.debug("Request to save SeatLock : {}", seatLockDTO);
        SeatLock seatLock = seatLockMapper.toEntity(seatLockDTO);
        seatLock = seatLockRepository.save(seatLock);
        return seatLockMapper.toDto(seatLock);
    }

    @Override
    public SeatLockDTO update(SeatLockDTO seatLockDTO) {
        LOG.debug("Request to update SeatLock : {}", seatLockDTO);
        SeatLock seatLock = seatLockMapper.toEntity(seatLockDTO);
        seatLock = seatLockRepository.save(seatLock);
        return seatLockMapper.toDto(seatLock);
    }

    @Override
    public Optional<SeatLockDTO> partialUpdate(SeatLockDTO seatLockDTO) {
        LOG.debug("Request to partially update SeatLock : {}", seatLockDTO);
        return seatLockRepository
                .findById(seatLockDTO.getId())
                .map(existingSeatLock -> {
                    seatLockMapper.partialUpdate(existingSeatLock, seatLockDTO);
                    return existingSeatLock;
                })
                .map(seatLockRepository::save)
                .map(seatLockMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeatLockDTO> findOne(Long id) {
        LOG.debug("Request to get SeatLock : {}", id);
        return seatLockRepository.findById(id).map(seatLockMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SeatLock : {}", id);
        seatLockRepository.deleteById(id);
    }

    // ===== try-hold (group-first) =====
    @Override
    public SeatHoldResponseDTO tryHold(SeatHoldRequestDTO req) {
        Instant now = Instant.now();
        int ttl = (req.getHoldTtlSec() == null || req.getHoldTtlSec() < 30) ? 180 : req.getHoldTtlSec();
        Instant expires = now.plusSeconds(ttl);

        Trip trip = tripRepository.findById(req.getTripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + req.getTripId()));

        List<String> seats = req.getSeatNumbers().stream()
                .map(SeatLockServiceImpl::normSeat).filter(Objects::nonNull).distinct().toList();
        String group = req.getLockGroupId();
        List<String> conflictingSeats = new ArrayList<>();

        // 1) Conflict check via Criteria: active HELD (not expired) OR COMMITTED
        if (hasAnyActiveConflict(trip.getId(), seats, now, group)) {
            // Find which seat conflicts to build a friendly message
            for (String seat : seats) {
                if (isSeatConflicting(trip.getId(), seat, now, group)) {
                    conflictingSeats.add(seat);
                }
            }

            if (!conflictingSeats.isEmpty()) {
                // Trả về message rõ ràng: Seat not available: A1, B2, C3
                return new SeatHoldResponseDTO(
                        "REJECTED",
                        "Seat not available: " + String.join(", ", conflictingSeats),
                        null,
                        null);
            }

            // fallback nếu không detect seat nào (trường hợp hiếm)
            return new SeatHoldResponseDTO(
                    "REJECTED",
                    "Seat(s) not available",
                    null,
                    null);
        }

        // 2) Idempotency by group prefix — if already HELD and unexpired, just return
        // them
        List<SeatLock> existing = findUnexpiredHeldByGroup(group, now);
        if (!existing.isEmpty()) {
            SeatHoldResponseDTO r = new SeatHoldResponseDTO();
            r.setStatus("HELD");
            r.setHeldSeats(existing.stream().map(SeatLock::getSeatNo).toList());
            r.setExpiresAt(existing.stream().map(SeatLock::getExpiresAt).min(Instant::compareTo).orElse(expires));
            r.setMessage("Already held");
            return r;
        }

        // 3) Create one row per seat; group via idempotencyKey prefix "<group>:<seat>"
        List<SeatLock> toSave = new ArrayList<>();
        for (String seat : seats) {
            SeatLock lock = new SeatLock();
            lock.setSeatNo(seat);
            lock.setUserId(req.getUserId());
            lock.setStatus(LockStatus.HELD);
            lock.setExpiresAt(expires);
            lock.setIdempotencyKey(group + ":" + seat); // <-- group prefix trick
            lock.setBookingId(null);
            lock.setCreatedAt(now);
            lock.setUpdatedAt(now);
            lock.setIsDeleted(Boolean.FALSE);
            lock.setTrip(trip);
            toSave.add(lock);
        }
        List<SeatLock> saved = seatLockRepository.saveAll(toSave);

        SeatHoldResponseDTO r = new SeatHoldResponseDTO();
        r.setStatus("HELD");
        r.setHeldSeats(saved.stream().map(SeatLock::getSeatNo).toList());
        r.setExpiresAt(expires);
        r.setMessage("Held " + saved.size() + " seats");
        return r;
    }

    // ===== attach-booking =====
    @Override
    public SeatLockActionResponseDTO attachBooking(AttachBookingRequestDTO req) {
        Instant now = Instant.now();
        List<SeatLock> held = findUnexpiredHeldByGroup(req.getLockGroupId(), now);
        if (held.isEmpty()) {
            return new SeatLockActionResponseDTO("NOT_FOUND", "No unexpired HELD locks for group");
        }
        int updated = 0;
        for (SeatLock l : held) {
            if (l.getBookingId() == null) {
                l.setBookingId(req.getBookingId());
                l.setUpdatedAt(now);
                updated++;
            }
        }
        seatLockRepository.saveAll(held);
        return new SeatLockActionResponseDTO("ATTACHED", "Attached bookingId to " + updated + " locks");
    }

    // ===== cancel-group (maps to EXPIRED since enum fixed) =====
    @Override
    public SeatLockActionResponseDTO cancelGroup(CancelGroupRequestDTO req) {
        if (req.getBookingId() == null && (req.getLockGroupId() == null || req.getLockGroupId().isBlank())) {
            return new SeatLockActionResponseDTO("BAD_REQUEST", "Provide bookingId or lockGroupId");
        }
        Instant now = Instant.now();
        List<SeatLock> held = (req.getBookingId() != null)
                ? findUnexpiredHeldByBooking(req.getBookingId(), now)
                : findUnexpiredHeldByGroup(req.getLockGroupId(), now);

        if (held.isEmpty()) {
            return new SeatLockActionResponseDTO("NOT_FOUND", "No unexpired HELD locks to cancel");
        }
        for (SeatLock l : held) {
            l.setStatus(LockStatus.EXPIRED);
            l.setExpiresAt(now);
            l.setUpdatedAt(now);
        }
        seatLockRepository.saveAll(held);
        return new SeatLockActionResponseDTO("RELEASED", "Cancelled " + held.size() + " held seats");
    }

    // ===== confirm-group (HELD -> COMMITTED) =====
    @Override
    public SeatLockActionResponseDTO confirmGroup(ConfirmGroupRequestDTO req) {
        if (req.getBookingId() == null && (req.getLockGroupId() == null || req.getLockGroupId().isBlank())) {
            return new SeatLockActionResponseDTO("BAD_REQUEST", "Provide bookingId or lockGroupId");
        }
        Instant now = Instant.now();
        List<SeatLock> held = (req.getBookingId() != null)
                ? findUnexpiredHeldByBooking(req.getBookingId(), now)
                : findUnexpiredHeldByGroup(req.getLockGroupId(), now);

        if (held.isEmpty()) {
            return new SeatLockActionResponseDTO("NOT_FOUND", "No unexpired HELD locks to confirm");
        }
        for (SeatLock l : held) {
            l.setStatus(LockStatus.COMMITTED);
            l.setUpdatedAt(now);
        }
        seatLockRepository.saveAll(held);
        return new SeatLockActionResponseDTO("CONFIRMED", "Committed " + held.size() + " seats");
    }

    // ===== active inspection =====
    @Transactional(readOnly = true)
    @Override
    public List<SeatLockDTO> findActive(Long tripId, Long bookingId, String lockGroupId) {
        Instant now = Instant.now();

        SeatLockCriteria c = new SeatLockCriteria();

        // tripId
        if (tripId != null) {
            LongFilter f = new LongFilter();
            f.setEquals(tripId);
            c.setTripId(f);
        }
        // bookingId
        if (bookingId != null) {
            LongFilter f = new LongFilter();
            f.setEquals(bookingId);
            c.setBookingId(f);
        }
        // group prefix via contains "<group>:"
        if (lockGroupId != null && !lockGroupId.isBlank()) {
            StringFilter f = new StringFilter();
            f.setContains(lockGroupId + ":");
            c.setIdempotencyKey(f);
        }
        // active = (HELD & not expired) OR COMMITTED
        // We'll return both and let caller filter; here we fetch both in two passes and
        // merge.

        // HELD & not expired
        SeatLockCriteria hc = c.copy();
        SeatLockCriteria.LockStatusFilter s = new SeatLockCriteria.LockStatusFilter();
        s.setEquals(LockStatus.HELD);
        hc.setStatus(s);
        InstantFilter exp = new InstantFilter();
        exp.setGreaterThan(now);
        hc.setExpiresAt(exp);
        List<SeatLock> held = queryService.findByCriteria(hc).stream().map(seatLockMapper::toEntity)
                .collect(Collectors.toList());

        // COMMITTED (no expiry check)
        SeatLockCriteria cc = c.copy();
        SeatLockCriteria.LockStatusFilter s2 = new SeatLockCriteria.LockStatusFilter();
        s2.setEquals(LockStatus.COMMITTED);
        cc.setStatus(s2);
        List<SeatLock> committed = queryService.findByCriteria(cc).stream().map(seatLockMapper::toEntity)
                .collect(Collectors.toList());

        // merge unique by id
        Map<Long, SeatLock> merged = new LinkedHashMap<>();
        (held == null ? List.<SeatLock>of() : held).forEach(l -> merged.put(l.getId(), l));
        (committed == null ? List.<SeatLock>of() : committed).forEach(l -> merged.put(l.getId(), l));

        return merged.values().stream().map(seatLockMapper::toDto).toList();
    }

    // ---------- helpers (Criteria only) ----------

    private boolean hasAnyActiveConflict(Long tripId, List<String> seats, Instant now, String groupPrefix) {
        for (String seat : seats) {
            if (isSeatConflicting(tripId, seat, now, groupPrefix))
                return true;
        }
        return false;
    }

    private boolean isSeatConflicting(Long tripId, String seat, Instant now, String groupPrefix) {
        // 1) active HELD & not expired, excluding our own group prefix
        List<SeatLock> activeHeld = queryService.findActiveLocksByTripAndSeats(tripId, List.of(seat), LockStatus.HELD,
                now);
        boolean heldConflict = activeHeld.stream()
                .anyMatch(l -> l.getIdempotencyKey() == null || !l.getIdempotencyKey().startsWith(groupPrefix + ":"));

        if (heldConflict)
            return true;

        // 2) any COMMITTED for this seat
        SeatLockCriteria c = new SeatLockCriteria();
        LongFilter tripF = new LongFilter();
        tripF.setEquals(tripId);
        c.setTripId(tripF);
        StringFilter seatF = new StringFilter();
        seatF.setEquals(seat);
        c.setSeatNo(seatF);
        SeatLockCriteria.LockStatusFilter statusF = new SeatLockCriteria.LockStatusFilter();
        statusF.setEquals(LockStatus.COMMITTED);
        c.setStatus(statusF);

        List<SeatLockDTO> committed = queryService.findByCriteria(c);
        return !committed.isEmpty();
    }

    private List<SeatLock> findUnexpiredHeldByGroup(String group, Instant now) {
        SeatLockCriteria c = new SeatLockCriteria();

        StringFilter key = new StringFilter();
        key.setContains(group + ":"); // LIKE %group:%
        c.setIdempotencyKey(key);

        SeatLockCriteria.LockStatusFilter status = new SeatLockCriteria.LockStatusFilter();
        status.setEquals(LockStatus.HELD);
        c.setStatus(status);

        InstantFilter exp = new InstantFilter();
        exp.setGreaterThan(now);
        c.setExpiresAt(exp);

        List<SeatLockDTO> dtos = queryService.findByCriteria(c);
        return dtos.stream().map(seatLockMapper::toEntity).toList();
    }

    private List<SeatLock> findUnexpiredHeldByBooking(Long bookingId, Instant now) {
        // Reuse existing helper:
        return queryService.findByBookingId(bookingId).stream()
                .filter(l -> l.getStatus() == LockStatus.HELD && l.getExpiresAt() != null
                        && l.getExpiresAt().isAfter(now))
                .toList();
    }

    // ===== Legacy interface methods (for backward compatibility) =====

    @Override
    public SeatLockResponseDTO tryLockSeats(SeatLockRequestDTO request) {
        LOG.debug("Legacy tryLockSeats called - converting to tryHold");

        // Convert legacy request to new format
        SeatHoldRequestDTO holdRequest = new SeatHoldRequestDTO();
        holdRequest.setTripId(request.getTripId());
        holdRequest.setSeatNumbers(request.getSeatNumbers());
        holdRequest.setLockGroupId(request.getIdemKey()); // Use idemKey as lockGroupId
        holdRequest.setIdemKey(request.getIdemKey());
        holdRequest.setUserId(request.getUserId()); // Not available in legacy request
        holdRequest.setHoldTtlSec(request.getHoldTtlSec()); // Use default

        SeatHoldResponseDTO holdResponse = tryHold(holdRequest);

        // Convert response back to legacy format
        SeatLockResponseDTO response = new SeatLockResponseDTO();
        response.setStatus(holdResponse.getStatus());
        response.setMessage(holdResponse.getMessage());
        response.setBookingId(request.getBookingId());
        response.setTripId(request.getTripId());
        response.setLockId(holdResponse.getExpiresAt() != null ? holdResponse.getExpiresAt().toString() : null);
        response.setExpiresAt(
                holdResponse.getExpiresAt() != null ? holdResponse.getExpiresAt().getEpochSecond() : null);

        return response;
    }

    @Override
    public SeatLockActionResponseDTO confirmSeatLocks(SeatLockActionRequestDTO request) {
        LOG.debug("Legacy confirmSeatLocks called - converting to confirmGroup");

        ConfirmGroupRequestDTO confirmRequest = new ConfirmGroupRequestDTO();
        confirmRequest.setBookingId(request.getBookingId());
        confirmRequest.setLockGroupId(null); // Not available in legacy request

        return confirmGroup(confirmRequest);
    }

    @Override
    public SeatLockActionResponseDTO cancelSeatLocks(SeatLockActionRequestDTO request) {
        LOG.debug("Legacy cancelSeatLocks called - converting to cancelGroup");

        CancelGroupRequestDTO cancelRequest = new CancelGroupRequestDTO();
        cancelRequest.setBookingId(request.getBookingId());
        cancelRequest.setLockGroupId(null); // Not available in legacy request

        return cancelGroup(cancelRequest);
    }

    @Override
    public SeatValidateLockResponseDTO validateAndLockSeats(SeatValidateLockRequestDTO request) {
        LOG.debug("Legacy validateAndLockSeats called - using tryHold with validation");

        // Convert to hold request for now - this is a simplified implementation
        SeatHoldRequestDTO holdRequest = new SeatHoldRequestDTO();
        holdRequest.setTripId(request.getTripId());
        holdRequest.setSeatNumbers(request.getSeatNumbers());
        holdRequest.setLockGroupId(request.getIdemKey());
        holdRequest.setIdemKey(request.getIdemKey());
        holdRequest.setUserId(null);
        holdRequest.setHoldTtlSec(null);

        SeatHoldResponseDTO holdResponse = tryHold(holdRequest);

        // Convert to validate response
        SeatValidateLockResponseDTO response = new SeatValidateLockResponseDTO();
        response.setStatus(holdResponse.getStatus());
        response.setMessage(holdResponse.getMessage());
        response.setTripId(request.getTripId());

        if ("HELD".equals(holdResponse.getStatus())) {
            response.setLockGroupId(request.getIdemKey());
            response.setExpiresAt(holdResponse.getExpiresAt());
            // Basic pricing info - would need actual pricing logic
            // response.setPricing(...);
        }

        return response;
    }

    @Override
    public SeatValidateLockResponseDTO validateSeatsOnly(SeatValidateLockRequestDTO request) {
        LOG.debug("Legacy validateSeatsOnly called - basic validation");

        try {
            // Check if trip exists
            tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + request.getTripId()));

            // For now, just return validated status - would need actual seat validation
            // logic
            SeatValidateLockResponseDTO response = new SeatValidateLockResponseDTO();
            response.setStatus("VALIDATED");
            response.setMessage("Seats validation completed");
            response.setTripId(request.getTripId());

            return response;

        } catch (EntityNotFoundException e) {
            return new SeatValidateLockResponseDTO("REJECTED", "Trip not found", request.getTripId());
        }
    }

    // ===== reclaim-expired-seats =====
    @Override
    public SeatLockActionResponseDTO reclaimExpiredSeats(ConfirmGroupRequestDTO req) {
        if (req.getBookingId() == null) {
            return new SeatLockActionResponseDTO("BAD_REQUEST", "Provide bookingId");
        }

        Instant now = Instant.now();

        // Find all seatlocks for the booking (regardless of status)
        List<SeatLock> bookingLocks = queryService.findByBookingId(req.getBookingId());

        if (bookingLocks.isEmpty()) {
            return new SeatLockActionResponseDTO("NOT_FOUND", "No seat locks found for booking");
        }

        List<SeatLock> reclaimedSeats = new ArrayList<>();

        for (SeatLock bookingLock : bookingLocks) {
            // Find expired seatlocks with same seatNo and trip
            List<SeatLock> expiredLocks = findExpiredLocksOrHeldBySeatAndTrip(
                    bookingLock.getSeatNo(),
                    bookingLock.getTrip().getId(),
                    now);

            for (SeatLock expiredLock : expiredLocks) {
                // Check if expired lock is not held by other bookingId and is actually expired
                if (expiredLock.getBookingId() == null ||
                        expiredLock.getBookingId().equals(req.getBookingId())) {

                    // Reclaim the expired seat for current booking
                    expiredLock.setBookingId(req.getBookingId());
                    expiredLock.setStatus(LockStatus.HELD);
                    expiredLock.setExpiresAt(now.plusSeconds(180)); // 3 minutes TTL
                    expiredLock.setUpdatedAt(now);
                    reclaimedSeats.add(expiredLock);
                }
            }
        }

        if (reclaimedSeats.isEmpty()) {
            return new SeatLockActionResponseDTO("NOT_FOUND", "No expired seats available to reclaim");
        }

        // Save all reclaimed seats
        seatLockRepository.saveAll(reclaimedSeats);

        return new SeatLockActionResponseDTO("RECLAIMED",
                "Reclaimed " + reclaimedSeats.size() + " expired seats");
    }

    // Helper method to find expired locks by seat number and trip
    List<SeatLock> findExpiredLocksOrHeldBySeatAndTrip(String seatNo, Long tripId, Instant now) {
        SeatLockCriteria c = new SeatLockCriteria();

        // Trip filter
        LongFilter tripF = new LongFilter();
        tripF.setEquals(tripId);
        c.setTripId(tripF);

        // Seat number filter
        StringFilter seatF = new StringFilter();
        seatF.setEquals(seatNo);
        c.setSeatNo(seatF);

        // Status filter for expired or held locks
        SeatLockCriteria.LockStatusFilter statusF = new SeatLockCriteria.LockStatusFilter();
        statusF.setIn(Arrays.asList(LockStatus.EXPIRED, LockStatus.HELD));
        c.setStatus(statusF);

        // For HELD status, find only expired ones
        InstantFilter exp = new InstantFilter();
        exp.setLessThan(now); // Expired before now
        c.setExpiresAt(exp);

        // Not deleted filter
        BooleanFilter deletedF = new BooleanFilter();
        deletedF.setEquals(false);
        c.setIsDeleted(deletedF);

        List<SeatLockDTO> dtos = queryService.findByCriteria(c);
        return dtos.stream().map(seatLockMapper::toEntity).toList();
    }
}
