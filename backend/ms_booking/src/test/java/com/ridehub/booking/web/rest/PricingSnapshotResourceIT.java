package com.ridehub.booking.web.rest;

import static com.ridehub.booking.domain.PricingSnapshotAsserts.*;
import static com.ridehub.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.booking.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.booking.IntegrationTest;
import com.ridehub.booking.domain.Booking;
import com.ridehub.booking.domain.PricingSnapshot;
import com.ridehub.booking.repository.PricingSnapshotRepository;
import com.ridehub.booking.service.dto.PricingSnapshotDTO;
import com.ridehub.booking.service.mapper.PricingSnapshotMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PricingSnapshotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PricingSnapshotResourceIT {

    private static final BigDecimal DEFAULT_BASE_FARE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BASE_FARE = new BigDecimal(2);
    private static final BigDecimal SMALLER_BASE_FARE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_VEHICLE_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_VEHICLE_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_VEHICLE_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_FLOOR_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_FLOOR_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_FLOOR_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_SEAT_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_SEAT_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_SEAT_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_SCHEDULE_OCCASION_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_SCHEDULE_OCCASION_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_SCHEDULE_OCCASION_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_FINAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_FINAL_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_FINAL_PRICE = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final UUID DEFAULT_DELETED_BY = UUID.randomUUID();
    private static final UUID UPDATED_DELETED_BY = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/pricing-snapshots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PricingSnapshotRepository pricingSnapshotRepository;

    @Autowired
    private PricingSnapshotMapper pricingSnapshotMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPricingSnapshotMockMvc;

    private PricingSnapshot pricingSnapshot;

    private PricingSnapshot insertedPricingSnapshot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PricingSnapshot createEntity(EntityManager em) {
        PricingSnapshot pricingSnapshot = new PricingSnapshot()
            .baseFare(DEFAULT_BASE_FARE)
            .vehicleFactor(DEFAULT_VEHICLE_FACTOR)
            .floorFactor(DEFAULT_FLOOR_FACTOR)
            .seatFactor(DEFAULT_SEAT_FACTOR)
            .scheduleOccasionFactor(DEFAULT_SCHEDULE_OCCASION_FACTOR)
            .finalPrice(DEFAULT_FINAL_PRICE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Booking booking;
        if (TestUtil.findAll(em, Booking.class).isEmpty()) {
            booking = BookingResourceIT.createEntity();
            em.persist(booking);
            em.flush();
        } else {
            booking = TestUtil.findAll(em, Booking.class).get(0);
        }
        pricingSnapshot.setBooking(booking);
        return pricingSnapshot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PricingSnapshot createUpdatedEntity(EntityManager em) {
        PricingSnapshot updatedPricingSnapshot = new PricingSnapshot()
            .baseFare(UPDATED_BASE_FARE)
            .vehicleFactor(UPDATED_VEHICLE_FACTOR)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .scheduleOccasionFactor(UPDATED_SCHEDULE_OCCASION_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Booking booking;
        if (TestUtil.findAll(em, Booking.class).isEmpty()) {
            booking = BookingResourceIT.createUpdatedEntity();
            em.persist(booking);
            em.flush();
        } else {
            booking = TestUtil.findAll(em, Booking.class).get(0);
        }
        updatedPricingSnapshot.setBooking(booking);
        return updatedPricingSnapshot;
    }

    @BeforeEach
    void initTest() {
        pricingSnapshot = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPricingSnapshot != null) {
            pricingSnapshotRepository.delete(insertedPricingSnapshot);
            insertedPricingSnapshot = null;
        }
    }

    @Test
    @Transactional
    void createPricingSnapshot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);
        var returnedPricingSnapshotDTO = om.readValue(
            restPricingSnapshotMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(pricingSnapshotDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PricingSnapshotDTO.class
        );

        // Validate the PricingSnapshot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPricingSnapshot = pricingSnapshotMapper.toEntity(returnedPricingSnapshotDTO);
        assertPricingSnapshotUpdatableFieldsEquals(returnedPricingSnapshot, getPersistedPricingSnapshot(returnedPricingSnapshot));

        insertedPricingSnapshot = returnedPricingSnapshot;
    }

    @Test
    @Transactional
    void createPricingSnapshotWithExistingId() throws Exception {
        // Create the PricingSnapshot with an existing ID
        pricingSnapshot.setId(1L);
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPricingSnapshotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBaseFareIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingSnapshot.setBaseFare(null);

        // Create the PricingSnapshot, which fails.
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        restPricingSnapshotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFinalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingSnapshot.setFinalPrice(null);

        // Create the PricingSnapshot, which fails.
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        restPricingSnapshotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingSnapshot.setCreatedAt(null);

        // Create the PricingSnapshot, which fails.
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        restPricingSnapshotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPricingSnapshots() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList
        restPricingSnapshotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pricingSnapshot.getId().intValue())))
            .andExpect(jsonPath("$.[*].baseFare").value(hasItem(sameNumber(DEFAULT_BASE_FARE))))
            .andExpect(jsonPath("$.[*].vehicleFactor").value(hasItem(sameNumber(DEFAULT_VEHICLE_FACTOR))))
            .andExpect(jsonPath("$.[*].floorFactor").value(hasItem(sameNumber(DEFAULT_FLOOR_FACTOR))))
            .andExpect(jsonPath("$.[*].seatFactor").value(hasItem(sameNumber(DEFAULT_SEAT_FACTOR))))
            .andExpect(jsonPath("$.[*].scheduleOccasionFactor").value(hasItem(sameNumber(DEFAULT_SCHEDULE_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(sameNumber(DEFAULT_FINAL_PRICE))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getPricingSnapshot() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get the pricingSnapshot
        restPricingSnapshotMockMvc
            .perform(get(ENTITY_API_URL_ID, pricingSnapshot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pricingSnapshot.getId().intValue()))
            .andExpect(jsonPath("$.baseFare").value(sameNumber(DEFAULT_BASE_FARE)))
            .andExpect(jsonPath("$.vehicleFactor").value(sameNumber(DEFAULT_VEHICLE_FACTOR)))
            .andExpect(jsonPath("$.floorFactor").value(sameNumber(DEFAULT_FLOOR_FACTOR)))
            .andExpect(jsonPath("$.seatFactor").value(sameNumber(DEFAULT_SEAT_FACTOR)))
            .andExpect(jsonPath("$.scheduleOccasionFactor").value(sameNumber(DEFAULT_SCHEDULE_OCCASION_FACTOR)))
            .andExpect(jsonPath("$.finalPrice").value(sameNumber(DEFAULT_FINAL_PRICE)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getPricingSnapshotsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        Long id = pricingSnapshot.getId();

        defaultPricingSnapshotFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPricingSnapshotFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPricingSnapshotFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare equals to
        defaultPricingSnapshotFiltering("baseFare.equals=" + DEFAULT_BASE_FARE, "baseFare.equals=" + UPDATED_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare in
        defaultPricingSnapshotFiltering("baseFare.in=" + DEFAULT_BASE_FARE + "," + UPDATED_BASE_FARE, "baseFare.in=" + UPDATED_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare is not null
        defaultPricingSnapshotFiltering("baseFare.specified=true", "baseFare.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare is greater than or equal to
        defaultPricingSnapshotFiltering(
            "baseFare.greaterThanOrEqual=" + DEFAULT_BASE_FARE,
            "baseFare.greaterThanOrEqual=" + UPDATED_BASE_FARE
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare is less than or equal to
        defaultPricingSnapshotFiltering("baseFare.lessThanOrEqual=" + DEFAULT_BASE_FARE, "baseFare.lessThanOrEqual=" + SMALLER_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare is less than
        defaultPricingSnapshotFiltering("baseFare.lessThan=" + UPDATED_BASE_FARE, "baseFare.lessThan=" + DEFAULT_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBaseFareIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where baseFare is greater than
        defaultPricingSnapshotFiltering("baseFare.greaterThan=" + SMALLER_BASE_FARE, "baseFare.greaterThan=" + DEFAULT_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor equals to
        defaultPricingSnapshotFiltering("vehicleFactor.equals=" + DEFAULT_VEHICLE_FACTOR, "vehicleFactor.equals=" + UPDATED_VEHICLE_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor in
        defaultPricingSnapshotFiltering(
            "vehicleFactor.in=" + DEFAULT_VEHICLE_FACTOR + "," + UPDATED_VEHICLE_FACTOR,
            "vehicleFactor.in=" + UPDATED_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor is not null
        defaultPricingSnapshotFiltering("vehicleFactor.specified=true", "vehicleFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor is greater than or equal to
        defaultPricingSnapshotFiltering(
            "vehicleFactor.greaterThanOrEqual=" + DEFAULT_VEHICLE_FACTOR,
            "vehicleFactor.greaterThanOrEqual=" + UPDATED_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor is less than or equal to
        defaultPricingSnapshotFiltering(
            "vehicleFactor.lessThanOrEqual=" + DEFAULT_VEHICLE_FACTOR,
            "vehicleFactor.lessThanOrEqual=" + SMALLER_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor is less than
        defaultPricingSnapshotFiltering(
            "vehicleFactor.lessThan=" + UPDATED_VEHICLE_FACTOR,
            "vehicleFactor.lessThan=" + DEFAULT_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByVehicleFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where vehicleFactor is greater than
        defaultPricingSnapshotFiltering(
            "vehicleFactor.greaterThan=" + SMALLER_VEHICLE_FACTOR,
            "vehicleFactor.greaterThan=" + DEFAULT_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor equals to
        defaultPricingSnapshotFiltering("floorFactor.equals=" + DEFAULT_FLOOR_FACTOR, "floorFactor.equals=" + UPDATED_FLOOR_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor in
        defaultPricingSnapshotFiltering(
            "floorFactor.in=" + DEFAULT_FLOOR_FACTOR + "," + UPDATED_FLOOR_FACTOR,
            "floorFactor.in=" + UPDATED_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor is not null
        defaultPricingSnapshotFiltering("floorFactor.specified=true", "floorFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor is greater than or equal to
        defaultPricingSnapshotFiltering(
            "floorFactor.greaterThanOrEqual=" + DEFAULT_FLOOR_FACTOR,
            "floorFactor.greaterThanOrEqual=" + UPDATED_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor is less than or equal to
        defaultPricingSnapshotFiltering(
            "floorFactor.lessThanOrEqual=" + DEFAULT_FLOOR_FACTOR,
            "floorFactor.lessThanOrEqual=" + SMALLER_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor is less than
        defaultPricingSnapshotFiltering("floorFactor.lessThan=" + UPDATED_FLOOR_FACTOR, "floorFactor.lessThan=" + DEFAULT_FLOOR_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFloorFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where floorFactor is greater than
        defaultPricingSnapshotFiltering(
            "floorFactor.greaterThan=" + SMALLER_FLOOR_FACTOR,
            "floorFactor.greaterThan=" + DEFAULT_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor equals to
        defaultPricingSnapshotFiltering("seatFactor.equals=" + DEFAULT_SEAT_FACTOR, "seatFactor.equals=" + UPDATED_SEAT_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor in
        defaultPricingSnapshotFiltering(
            "seatFactor.in=" + DEFAULT_SEAT_FACTOR + "," + UPDATED_SEAT_FACTOR,
            "seatFactor.in=" + UPDATED_SEAT_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor is not null
        defaultPricingSnapshotFiltering("seatFactor.specified=true", "seatFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor is greater than or equal to
        defaultPricingSnapshotFiltering(
            "seatFactor.greaterThanOrEqual=" + DEFAULT_SEAT_FACTOR,
            "seatFactor.greaterThanOrEqual=" + UPDATED_SEAT_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor is less than or equal to
        defaultPricingSnapshotFiltering(
            "seatFactor.lessThanOrEqual=" + DEFAULT_SEAT_FACTOR,
            "seatFactor.lessThanOrEqual=" + SMALLER_SEAT_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor is less than
        defaultPricingSnapshotFiltering("seatFactor.lessThan=" + UPDATED_SEAT_FACTOR, "seatFactor.lessThan=" + DEFAULT_SEAT_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsBySeatFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where seatFactor is greater than
        defaultPricingSnapshotFiltering("seatFactor.greaterThan=" + SMALLER_SEAT_FACTOR, "seatFactor.greaterThan=" + DEFAULT_SEAT_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor equals to
        defaultPricingSnapshotFiltering(
            "scheduleOccasionFactor.equals=" + DEFAULT_SCHEDULE_OCCASION_FACTOR,
            "scheduleOccasionFactor.equals=" + UPDATED_SCHEDULE_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor in
        defaultPricingSnapshotFiltering(
            "scheduleOccasionFactor.in=" + DEFAULT_SCHEDULE_OCCASION_FACTOR + "," + UPDATED_SCHEDULE_OCCASION_FACTOR,
            "scheduleOccasionFactor.in=" + UPDATED_SCHEDULE_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor is not null
        defaultPricingSnapshotFiltering("scheduleOccasionFactor.specified=true", "scheduleOccasionFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor is greater than or equal to
        defaultPricingSnapshotFiltering(
            "scheduleOccasionFactor.greaterThanOrEqual=" + DEFAULT_SCHEDULE_OCCASION_FACTOR,
            "scheduleOccasionFactor.greaterThanOrEqual=" + UPDATED_SCHEDULE_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor is less than or equal to
        defaultPricingSnapshotFiltering(
            "scheduleOccasionFactor.lessThanOrEqual=" + DEFAULT_SCHEDULE_OCCASION_FACTOR,
            "scheduleOccasionFactor.lessThanOrEqual=" + SMALLER_SCHEDULE_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor is less than
        defaultPricingSnapshotFiltering(
            "scheduleOccasionFactor.lessThan=" + UPDATED_SCHEDULE_OCCASION_FACTOR,
            "scheduleOccasionFactor.lessThan=" + DEFAULT_SCHEDULE_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByScheduleOccasionFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where scheduleOccasionFactor is greater than
        defaultPricingSnapshotFiltering(
            "scheduleOccasionFactor.greaterThan=" + SMALLER_SCHEDULE_OCCASION_FACTOR,
            "scheduleOccasionFactor.greaterThan=" + DEFAULT_SCHEDULE_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice equals to
        defaultPricingSnapshotFiltering("finalPrice.equals=" + DEFAULT_FINAL_PRICE, "finalPrice.equals=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice in
        defaultPricingSnapshotFiltering(
            "finalPrice.in=" + DEFAULT_FINAL_PRICE + "," + UPDATED_FINAL_PRICE,
            "finalPrice.in=" + UPDATED_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice is not null
        defaultPricingSnapshotFiltering("finalPrice.specified=true", "finalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice is greater than or equal to
        defaultPricingSnapshotFiltering(
            "finalPrice.greaterThanOrEqual=" + DEFAULT_FINAL_PRICE,
            "finalPrice.greaterThanOrEqual=" + UPDATED_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice is less than or equal to
        defaultPricingSnapshotFiltering(
            "finalPrice.lessThanOrEqual=" + DEFAULT_FINAL_PRICE,
            "finalPrice.lessThanOrEqual=" + SMALLER_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice is less than
        defaultPricingSnapshotFiltering("finalPrice.lessThan=" + UPDATED_FINAL_PRICE, "finalPrice.lessThan=" + DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByFinalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where finalPrice is greater than
        defaultPricingSnapshotFiltering("finalPrice.greaterThan=" + SMALLER_FINAL_PRICE, "finalPrice.greaterThan=" + DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where createdAt equals to
        defaultPricingSnapshotFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where createdAt in
        defaultPricingSnapshotFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where createdAt is not null
        defaultPricingSnapshotFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where updatedAt equals to
        defaultPricingSnapshotFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where updatedAt in
        defaultPricingSnapshotFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where updatedAt is not null
        defaultPricingSnapshotFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where isDeleted equals to
        defaultPricingSnapshotFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where isDeleted in
        defaultPricingSnapshotFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where isDeleted is not null
        defaultPricingSnapshotFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where deletedAt equals to
        defaultPricingSnapshotFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where deletedAt in
        defaultPricingSnapshotFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where deletedAt is not null
        defaultPricingSnapshotFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where deletedBy equals to
        defaultPricingSnapshotFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where deletedBy in
        defaultPricingSnapshotFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        // Get all the pricingSnapshotList where deletedBy is not null
        defaultPricingSnapshotFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingSnapshotsByBookingIsEqualToSomething() throws Exception {
        Booking booking;
        if (TestUtil.findAll(em, Booking.class).isEmpty()) {
            pricingSnapshotRepository.saveAndFlush(pricingSnapshot);
            booking = BookingResourceIT.createEntity();
        } else {
            booking = TestUtil.findAll(em, Booking.class).get(0);
        }
        em.persist(booking);
        em.flush();
        pricingSnapshot.setBooking(booking);
        pricingSnapshotRepository.saveAndFlush(pricingSnapshot);
        Long bookingId = booking.getId();
        // Get all the pricingSnapshotList where booking equals to bookingId
        defaultPricingSnapshotShouldBeFound("bookingId.equals=" + bookingId);

        // Get all the pricingSnapshotList where booking equals to (bookingId + 1)
        defaultPricingSnapshotShouldNotBeFound("bookingId.equals=" + (bookingId + 1));
    }

    private void defaultPricingSnapshotFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPricingSnapshotShouldBeFound(shouldBeFound);
        defaultPricingSnapshotShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPricingSnapshotShouldBeFound(String filter) throws Exception {
        restPricingSnapshotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pricingSnapshot.getId().intValue())))
            .andExpect(jsonPath("$.[*].baseFare").value(hasItem(sameNumber(DEFAULT_BASE_FARE))))
            .andExpect(jsonPath("$.[*].vehicleFactor").value(hasItem(sameNumber(DEFAULT_VEHICLE_FACTOR))))
            .andExpect(jsonPath("$.[*].floorFactor").value(hasItem(sameNumber(DEFAULT_FLOOR_FACTOR))))
            .andExpect(jsonPath("$.[*].seatFactor").value(hasItem(sameNumber(DEFAULT_SEAT_FACTOR))))
            .andExpect(jsonPath("$.[*].scheduleOccasionFactor").value(hasItem(sameNumber(DEFAULT_SCHEDULE_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(sameNumber(DEFAULT_FINAL_PRICE))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restPricingSnapshotMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPricingSnapshotShouldNotBeFound(String filter) throws Exception {
        restPricingSnapshotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPricingSnapshotMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPricingSnapshot() throws Exception {
        // Get the pricingSnapshot
        restPricingSnapshotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPricingSnapshot() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricingSnapshot
        PricingSnapshot updatedPricingSnapshot = pricingSnapshotRepository.findById(pricingSnapshot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPricingSnapshot are not directly saved in db
        em.detach(updatedPricingSnapshot);
        updatedPricingSnapshot
            .baseFare(UPDATED_BASE_FARE)
            .vehicleFactor(UPDATED_VEHICLE_FACTOR)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .scheduleOccasionFactor(UPDATED_SCHEDULE_OCCASION_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(updatedPricingSnapshot);

        restPricingSnapshotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pricingSnapshotDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isOk());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPricingSnapshotToMatchAllProperties(updatedPricingSnapshot);
    }

    @Test
    @Transactional
    void putNonExistingPricingSnapshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingSnapshot.setId(longCount.incrementAndGet());

        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPricingSnapshotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pricingSnapshotDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPricingSnapshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingSnapshot.setId(longCount.incrementAndGet());

        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingSnapshotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPricingSnapshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingSnapshot.setId(longCount.incrementAndGet());

        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingSnapshotMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePricingSnapshotWithPatch() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricingSnapshot using partial update
        PricingSnapshot partialUpdatedPricingSnapshot = new PricingSnapshot();
        partialUpdatedPricingSnapshot.setId(pricingSnapshot.getId());

        partialUpdatedPricingSnapshot
            .scheduleOccasionFactor(UPDATED_SCHEDULE_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restPricingSnapshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPricingSnapshot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPricingSnapshot))
            )
            .andExpect(status().isOk());

        // Validate the PricingSnapshot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPricingSnapshotUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPricingSnapshot, pricingSnapshot),
            getPersistedPricingSnapshot(pricingSnapshot)
        );
    }

    @Test
    @Transactional
    void fullUpdatePricingSnapshotWithPatch() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricingSnapshot using partial update
        PricingSnapshot partialUpdatedPricingSnapshot = new PricingSnapshot();
        partialUpdatedPricingSnapshot.setId(pricingSnapshot.getId());

        partialUpdatedPricingSnapshot
            .baseFare(UPDATED_BASE_FARE)
            .vehicleFactor(UPDATED_VEHICLE_FACTOR)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .scheduleOccasionFactor(UPDATED_SCHEDULE_OCCASION_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restPricingSnapshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPricingSnapshot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPricingSnapshot))
            )
            .andExpect(status().isOk());

        // Validate the PricingSnapshot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPricingSnapshotUpdatableFieldsEquals(
            partialUpdatedPricingSnapshot,
            getPersistedPricingSnapshot(partialUpdatedPricingSnapshot)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPricingSnapshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingSnapshot.setId(longCount.incrementAndGet());

        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPricingSnapshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pricingSnapshotDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPricingSnapshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingSnapshot.setId(longCount.incrementAndGet());

        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingSnapshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPricingSnapshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingSnapshot.setId(longCount.incrementAndGet());

        // Create the PricingSnapshot
        PricingSnapshotDTO pricingSnapshotDTO = pricingSnapshotMapper.toDto(pricingSnapshot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingSnapshotMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricingSnapshotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PricingSnapshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePricingSnapshot() throws Exception {
        // Initialize the database
        insertedPricingSnapshot = pricingSnapshotRepository.saveAndFlush(pricingSnapshot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pricingSnapshot
        restPricingSnapshotMockMvc
            .perform(delete(ENTITY_API_URL_ID, pricingSnapshot.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pricingSnapshotRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected PricingSnapshot getPersistedPricingSnapshot(PricingSnapshot pricingSnapshot) {
        return pricingSnapshotRepository.findById(pricingSnapshot.getId()).orElseThrow();
    }

    protected void assertPersistedPricingSnapshotToMatchAllProperties(PricingSnapshot expectedPricingSnapshot) {
        assertPricingSnapshotAllPropertiesEquals(expectedPricingSnapshot, getPersistedPricingSnapshot(expectedPricingSnapshot));
    }

    protected void assertPersistedPricingSnapshotToMatchUpdatableProperties(PricingSnapshot expectedPricingSnapshot) {
        assertPricingSnapshotAllUpdatablePropertiesEquals(expectedPricingSnapshot, getPersistedPricingSnapshot(expectedPricingSnapshot));
    }
}
