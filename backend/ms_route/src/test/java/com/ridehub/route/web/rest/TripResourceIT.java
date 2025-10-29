package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.TripAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.route.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Attendant;
import com.ridehub.route.domain.Driver;
import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.domain.Trip;
import com.ridehub.route.domain.Vehicle;
import com.ridehub.route.repository.TripRepository;
import com.ridehub.route.service.dto.TripDTO;
import com.ridehub.route.service.mapper.TripMapper;
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
 * Integration tests for the {@link TripResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TripResourceIT {

    private static final String DEFAULT_TRIP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TRIP_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DEPARTURE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPARTURE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ARRIVAL_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ARRIVAL_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_OCCASION_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_OCCASION_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_OCCASION_FACTOR = new BigDecimal(1 - 1);

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

    private static final String ENTITY_API_URL = "/api/trips";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTripMockMvc;

    private Trip trip;

    private Trip insertedTrip;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trip createEntity(EntityManager em) {
        Trip trip = new Trip()
            .tripCode(DEFAULT_TRIP_CODE)
            .departureTime(DEFAULT_DEPARTURE_TIME)
            .arrivalTime(DEFAULT_ARRIVAL_TIME)
            .occasionFactor(DEFAULT_OCCASION_FACTOR)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            route = RouteResourceIT.createEntity(em);
            em.persist(route);
            em.flush();
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        trip.setRoute(route);
        // Add required entity
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            vehicle = VehicleResourceIT.createEntity(em);
            em.persist(vehicle);
            em.flush();
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        trip.setVehicle(vehicle);
        // Add required entity
        ScheduleTimeSlot scheduleTimeSlot;
        if (TestUtil.findAll(em, ScheduleTimeSlot.class).isEmpty()) {
            scheduleTimeSlot = ScheduleTimeSlotResourceIT.createEntity(em);
            em.persist(scheduleTimeSlot);
            em.flush();
        } else {
            scheduleTimeSlot = TestUtil.findAll(em, ScheduleTimeSlot.class).get(0);
        }
        trip.setSlot(scheduleTimeSlot);
        // Add required entity
        Driver driver;
        if (TestUtil.findAll(em, Driver.class).isEmpty()) {
            driver = DriverResourceIT.createEntity(em);
            em.persist(driver);
            em.flush();
        } else {
            driver = TestUtil.findAll(em, Driver.class).get(0);
        }
        trip.setDriver(driver);
        return trip;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trip createUpdatedEntity(EntityManager em) {
        Trip updatedTrip = new Trip()
            .tripCode(UPDATED_TRIP_CODE)
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            route = RouteResourceIT.createUpdatedEntity(em);
            em.persist(route);
            em.flush();
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        updatedTrip.setRoute(route);
        // Add required entity
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            vehicle = VehicleResourceIT.createUpdatedEntity(em);
            em.persist(vehicle);
            em.flush();
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        updatedTrip.setVehicle(vehicle);
        // Add required entity
        ScheduleTimeSlot scheduleTimeSlot;
        if (TestUtil.findAll(em, ScheduleTimeSlot.class).isEmpty()) {
            scheduleTimeSlot = ScheduleTimeSlotResourceIT.createUpdatedEntity(em);
            em.persist(scheduleTimeSlot);
            em.flush();
        } else {
            scheduleTimeSlot = TestUtil.findAll(em, ScheduleTimeSlot.class).get(0);
        }
        updatedTrip.setSlot(scheduleTimeSlot);
        // Add required entity
        Driver driver;
        if (TestUtil.findAll(em, Driver.class).isEmpty()) {
            driver = DriverResourceIT.createUpdatedEntity(em);
            em.persist(driver);
            em.flush();
        } else {
            driver = TestUtil.findAll(em, Driver.class).get(0);
        }
        updatedTrip.setDriver(driver);
        return updatedTrip;
    }

    @BeforeEach
    void initTest() {
        trip = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTrip != null) {
            tripRepository.delete(insertedTrip);
            insertedTrip = null;
        }
    }

    @Test
    @Transactional
    void createTrip() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);
        var returnedTripDTO = om.readValue(
            restTripMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TripDTO.class
        );

        // Validate the Trip in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrip = tripMapper.toEntity(returnedTripDTO);
        assertTripUpdatableFieldsEquals(returnedTrip, getPersistedTrip(returnedTrip));

        insertedTrip = returnedTrip;
    }

    @Test
    @Transactional
    void createTripWithExistingId() throws Exception {
        // Create the Trip with an existing ID
        trip.setId(1L);
        TripDTO tripDTO = tripMapper.toDto(trip);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTripCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setTripCode(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDepartureTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setDepartureTime(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArrivalTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setArrivalTime(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOccasionFactorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setOccasionFactor(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setCreatedAt(null);

        // Create the Trip, which fails.
        TripDTO tripDTO = tripMapper.toDto(trip);

        restTripMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTrips() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList
        restTripMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].tripCode").value(hasItem(DEFAULT_TRIP_CODE)))
            .andExpect(jsonPath("$.[*].departureTime").value(hasItem(DEFAULT_DEPARTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].arrivalTime").value(hasItem(DEFAULT_ARRIVAL_TIME.toString())))
            .andExpect(jsonPath("$.[*].occasionFactor").value(hasItem(sameNumber(DEFAULT_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getTrip() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get the trip
        restTripMockMvc
            .perform(get(ENTITY_API_URL_ID, trip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trip.getId().intValue()))
            .andExpect(jsonPath("$.tripCode").value(DEFAULT_TRIP_CODE))
            .andExpect(jsonPath("$.departureTime").value(DEFAULT_DEPARTURE_TIME.toString()))
            .andExpect(jsonPath("$.arrivalTime").value(DEFAULT_ARRIVAL_TIME.toString()))
            .andExpect(jsonPath("$.occasionFactor").value(sameNumber(DEFAULT_OCCASION_FACTOR)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getTripsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        Long id = trip.getId();

        defaultTripFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTripFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTripFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTripsByTripCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where tripCode equals to
        defaultTripFiltering("tripCode.equals=" + DEFAULT_TRIP_CODE, "tripCode.equals=" + UPDATED_TRIP_CODE);
    }

    @Test
    @Transactional
    void getAllTripsByTripCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where tripCode in
        defaultTripFiltering("tripCode.in=" + DEFAULT_TRIP_CODE + "," + UPDATED_TRIP_CODE, "tripCode.in=" + UPDATED_TRIP_CODE);
    }

    @Test
    @Transactional
    void getAllTripsByTripCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where tripCode is not null
        defaultTripFiltering("tripCode.specified=true", "tripCode.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByTripCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where tripCode contains
        defaultTripFiltering("tripCode.contains=" + DEFAULT_TRIP_CODE, "tripCode.contains=" + UPDATED_TRIP_CODE);
    }

    @Test
    @Transactional
    void getAllTripsByTripCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where tripCode does not contain
        defaultTripFiltering("tripCode.doesNotContain=" + UPDATED_TRIP_CODE, "tripCode.doesNotContain=" + DEFAULT_TRIP_CODE);
    }

    @Test
    @Transactional
    void getAllTripsByDepartureTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where departureTime equals to
        defaultTripFiltering("departureTime.equals=" + DEFAULT_DEPARTURE_TIME, "departureTime.equals=" + UPDATED_DEPARTURE_TIME);
    }

    @Test
    @Transactional
    void getAllTripsByDepartureTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where departureTime in
        defaultTripFiltering(
            "departureTime.in=" + DEFAULT_DEPARTURE_TIME + "," + UPDATED_DEPARTURE_TIME,
            "departureTime.in=" + UPDATED_DEPARTURE_TIME
        );
    }

    @Test
    @Transactional
    void getAllTripsByDepartureTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where departureTime is not null
        defaultTripFiltering("departureTime.specified=true", "departureTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByArrivalTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where arrivalTime equals to
        defaultTripFiltering("arrivalTime.equals=" + DEFAULT_ARRIVAL_TIME, "arrivalTime.equals=" + UPDATED_ARRIVAL_TIME);
    }

    @Test
    @Transactional
    void getAllTripsByArrivalTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where arrivalTime in
        defaultTripFiltering(
            "arrivalTime.in=" + DEFAULT_ARRIVAL_TIME + "," + UPDATED_ARRIVAL_TIME,
            "arrivalTime.in=" + UPDATED_ARRIVAL_TIME
        );
    }

    @Test
    @Transactional
    void getAllTripsByArrivalTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where arrivalTime is not null
        defaultTripFiltering("arrivalTime.specified=true", "arrivalTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor equals to
        defaultTripFiltering("occasionFactor.equals=" + DEFAULT_OCCASION_FACTOR, "occasionFactor.equals=" + UPDATED_OCCASION_FACTOR);
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor in
        defaultTripFiltering(
            "occasionFactor.in=" + DEFAULT_OCCASION_FACTOR + "," + UPDATED_OCCASION_FACTOR,
            "occasionFactor.in=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor is not null
        defaultTripFiltering("occasionFactor.specified=true", "occasionFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor is greater than or equal to
        defaultTripFiltering(
            "occasionFactor.greaterThanOrEqual=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.greaterThanOrEqual=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor is less than or equal to
        defaultTripFiltering(
            "occasionFactor.lessThanOrEqual=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.lessThanOrEqual=" + SMALLER_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor is less than
        defaultTripFiltering("occasionFactor.lessThan=" + UPDATED_OCCASION_FACTOR, "occasionFactor.lessThan=" + DEFAULT_OCCASION_FACTOR);
    }

    @Test
    @Transactional
    void getAllTripsByOccasionFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where occasionFactor is greater than
        defaultTripFiltering(
            "occasionFactor.greaterThan=" + SMALLER_OCCASION_FACTOR,
            "occasionFactor.greaterThan=" + DEFAULT_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllTripsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where createdAt equals to
        defaultTripFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTripsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where createdAt in
        defaultTripFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTripsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where createdAt is not null
        defaultTripFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where updatedAt equals to
        defaultTripFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTripsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where updatedAt in
        defaultTripFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTripsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where updatedAt is not null
        defaultTripFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where isDeleted equals to
        defaultTripFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllTripsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where isDeleted in
        defaultTripFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllTripsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where isDeleted is not null
        defaultTripFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where deletedAt equals to
        defaultTripFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllTripsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where deletedAt in
        defaultTripFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllTripsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where deletedAt is not null
        defaultTripFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where deletedBy equals to
        defaultTripFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllTripsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where deletedBy in
        defaultTripFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllTripsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList where deletedBy is not null
        defaultTripFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTripsByRouteIsEqualToSomething() throws Exception {
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            tripRepository.saveAndFlush(trip);
            route = RouteResourceIT.createEntity(em);
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        em.persist(route);
        em.flush();
        trip.setRoute(route);
        tripRepository.saveAndFlush(trip);
        Long routeId = route.getId();
        // Get all the tripList where route equals to routeId
        defaultTripShouldBeFound("routeId.equals=" + routeId);

        // Get all the tripList where route equals to (routeId + 1)
        defaultTripShouldNotBeFound("routeId.equals=" + (routeId + 1));
    }

    @Test
    @Transactional
    void getAllTripsByVehicleIsEqualToSomething() throws Exception {
        Vehicle vehicle;
        if (TestUtil.findAll(em, Vehicle.class).isEmpty()) {
            tripRepository.saveAndFlush(trip);
            vehicle = VehicleResourceIT.createEntity(em);
        } else {
            vehicle = TestUtil.findAll(em, Vehicle.class).get(0);
        }
        em.persist(vehicle);
        em.flush();
        trip.setVehicle(vehicle);
        tripRepository.saveAndFlush(trip);
        Long vehicleId = vehicle.getId();
        // Get all the tripList where vehicle equals to vehicleId
        defaultTripShouldBeFound("vehicleId.equals=" + vehicleId);

        // Get all the tripList where vehicle equals to (vehicleId + 1)
        defaultTripShouldNotBeFound("vehicleId.equals=" + (vehicleId + 1));
    }

    @Test
    @Transactional
    void getAllTripsBySlotIsEqualToSomething() throws Exception {
        ScheduleTimeSlot slot;
        if (TestUtil.findAll(em, ScheduleTimeSlot.class).isEmpty()) {
            tripRepository.saveAndFlush(trip);
            slot = ScheduleTimeSlotResourceIT.createEntity(em);
        } else {
            slot = TestUtil.findAll(em, ScheduleTimeSlot.class).get(0);
        }
        em.persist(slot);
        em.flush();
        trip.setSlot(slot);
        tripRepository.saveAndFlush(trip);
        Long slotId = slot.getId();
        // Get all the tripList where slot equals to slotId
        defaultTripShouldBeFound("slotId.equals=" + slotId);

        // Get all the tripList where slot equals to (slotId + 1)
        defaultTripShouldNotBeFound("slotId.equals=" + (slotId + 1));
    }

    @Test
    @Transactional
    void getAllTripsByDriverIsEqualToSomething() throws Exception {
        Driver driver;
        if (TestUtil.findAll(em, Driver.class).isEmpty()) {
            tripRepository.saveAndFlush(trip);
            driver = DriverResourceIT.createEntity(em);
        } else {
            driver = TestUtil.findAll(em, Driver.class).get(0);
        }
        em.persist(driver);
        em.flush();
        trip.setDriver(driver);
        tripRepository.saveAndFlush(trip);
        Long driverId = driver.getId();
        // Get all the tripList where driver equals to driverId
        defaultTripShouldBeFound("driverId.equals=" + driverId);

        // Get all the tripList where driver equals to (driverId + 1)
        defaultTripShouldNotBeFound("driverId.equals=" + (driverId + 1));
    }

    @Test
    @Transactional
    void getAllTripsByAttendantIsEqualToSomething() throws Exception {
        Attendant attendant;
        if (TestUtil.findAll(em, Attendant.class).isEmpty()) {
            tripRepository.saveAndFlush(trip);
            attendant = AttendantResourceIT.createEntity(em);
        } else {
            attendant = TestUtil.findAll(em, Attendant.class).get(0);
        }
        em.persist(attendant);
        em.flush();
        trip.setAttendant(attendant);
        tripRepository.saveAndFlush(trip);
        Long attendantId = attendant.getId();
        // Get all the tripList where attendant equals to attendantId
        defaultTripShouldBeFound("attendantId.equals=" + attendantId);

        // Get all the tripList where attendant equals to (attendantId + 1)
        defaultTripShouldNotBeFound("attendantId.equals=" + (attendantId + 1));
    }

    private void defaultTripFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTripShouldBeFound(shouldBeFound);
        defaultTripShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTripShouldBeFound(String filter) throws Exception {
        restTripMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].tripCode").value(hasItem(DEFAULT_TRIP_CODE)))
            .andExpect(jsonPath("$.[*].departureTime").value(hasItem(DEFAULT_DEPARTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].arrivalTime").value(hasItem(DEFAULT_ARRIVAL_TIME.toString())))
            .andExpect(jsonPath("$.[*].occasionFactor").value(hasItem(sameNumber(DEFAULT_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restTripMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTripShouldNotBeFound(String filter) throws Exception {
        restTripMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTripMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTrip() throws Exception {
        // Get the trip
        restTripMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTrip() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trip
        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTrip are not directly saved in db
        em.detach(updatedTrip);
        updatedTrip
            .tripCode(UPDATED_TRIP_CODE)
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        TripDTO tripDTO = tripMapper.toDto(updatedTrip);

        restTripMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripDTO))
            )
            .andExpect(status().isOk());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTripToMatchAllProperties(updatedTrip);
    }

    @Test
    @Transactional
    void putNonExistingTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTripWithPatch() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trip using partial update
        Trip partialUpdatedTrip = new Trip();
        partialUpdatedTrip.setId(trip.getId());

        partialUpdatedTrip.isDeleted(UPDATED_IS_DELETED).deletedBy(UPDATED_DELETED_BY);

        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrip.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrip))
            )
            .andExpect(status().isOk());

        // Validate the Trip in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrip, trip), getPersistedTrip(trip));
    }

    @Test
    @Transactional
    void fullUpdateTripWithPatch() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trip using partial update
        Trip partialUpdatedTrip = new Trip();
        partialUpdatedTrip.setId(trip.getId());

        partialUpdatedTrip
            .tripCode(UPDATED_TRIP_CODE)
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrip.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrip))
            )
            .andExpect(status().isOk());

        // Validate the Trip in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripUpdatableFieldsEquals(partialUpdatedTrip, getPersistedTrip(partialUpdatedTrip));
    }

    @Test
    @Transactional
    void patchNonExistingTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tripDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // Create the Trip
        TripDTO tripDTO = tripMapper.toDto(trip);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tripDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrip() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trip
        restTripMockMvc
            .perform(delete(ENTITY_API_URL_ID, trip.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tripRepository.count();
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

    protected Trip getPersistedTrip(Trip trip) {
        return tripRepository.findById(trip.getId()).orElseThrow();
    }

    protected void assertPersistedTripToMatchAllProperties(Trip expectedTrip) {
        assertTripAllPropertiesEquals(expectedTrip, getPersistedTrip(expectedTrip));
    }

    protected void assertPersistedTripToMatchUpdatableProperties(Trip expectedTrip) {
        assertTripAllUpdatablePropertiesEquals(expectedTrip, getPersistedTrip(expectedTrip));
    }
}
