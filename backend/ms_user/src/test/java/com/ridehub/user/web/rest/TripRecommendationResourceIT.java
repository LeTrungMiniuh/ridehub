package com.ridehub.user.web.rest;

import static com.ridehub.user.domain.TripRecommendationAsserts.*;
import static com.ridehub.user.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.user.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.user.IntegrationTest;
import com.ridehub.user.domain.AppUser;
import com.ridehub.user.domain.TripRecommendation;
import com.ridehub.user.repository.TripRecommendationRepository;
import com.ridehub.user.service.dto.TripRecommendationDTO;
import com.ridehub.user.service.mapper.TripRecommendationMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
 * Integration tests for the {@link TripRecommendationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TripRecommendationResourceIT {

    private static final String DEFAULT_ORIGIN = "AAAAAAAAAA";
    private static final String UPDATED_ORIGIN = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATION = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_TRAVEL_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TRAVEL_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TRAVEL_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalTime DEFAULT_PREFERRED_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_PREFERRED_TIME = LocalTime.MAX.withNano(0);

    private static final String DEFAULT_BUDGET_RANGE = "AAAAAAAAAA";
    private static final String UPDATED_BUDGET_RANGE = "BBBBBBBBBB";

    private static final String DEFAULT_SEAT_PREFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_SEAT_PREFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_RECOMMENDED_TRIPS = "AAAAAAAAAA";
    private static final String UPDATED_RECOMMENDED_TRIPS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_CONFIDENCE_SCORE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CONFIDENCE_SCORE = new BigDecimal(2);
    private static final BigDecimal SMALLER_CONFIDENCE_SCORE = new BigDecimal(1 - 1);

    private static final Boolean DEFAULT_IS_BOOKED = false;
    private static final Boolean UPDATED_IS_BOOKED = true;

    private static final Integer DEFAULT_FEEDBACK_RATING = 1;
    private static final Integer UPDATED_FEEDBACK_RATING = 2;
    private static final Integer SMALLER_FEEDBACK_RATING = 1 - 1;

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

    private static final String ENTITY_API_URL = "/api/trip-recommendations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TripRecommendationRepository tripRecommendationRepository;

    @Autowired
    private TripRecommendationMapper tripRecommendationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTripRecommendationMockMvc;

    private TripRecommendation tripRecommendation;

    private TripRecommendation insertedTripRecommendation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TripRecommendation createEntity(EntityManager em) {
        TripRecommendation tripRecommendation = new TripRecommendation()
            .origin(DEFAULT_ORIGIN)
            .destination(DEFAULT_DESTINATION)
            .travelDate(DEFAULT_TRAVEL_DATE)
            .preferredTime(DEFAULT_PREFERRED_TIME)
            .budgetRange(DEFAULT_BUDGET_RANGE)
            .seatPreference(DEFAULT_SEAT_PREFERENCE)
            .recommendedTrips(DEFAULT_RECOMMENDED_TRIPS)
            .confidenceScore(DEFAULT_CONFIDENCE_SCORE)
            .isBooked(DEFAULT_IS_BOOKED)
            .feedbackRating(DEFAULT_FEEDBACK_RATING)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createEntity();
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        tripRecommendation.setUser(appUser);
        return tripRecommendation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TripRecommendation createUpdatedEntity(EntityManager em) {
        TripRecommendation updatedTripRecommendation = new TripRecommendation()
            .origin(UPDATED_ORIGIN)
            .destination(UPDATED_DESTINATION)
            .travelDate(UPDATED_TRAVEL_DATE)
            .preferredTime(UPDATED_PREFERRED_TIME)
            .budgetRange(UPDATED_BUDGET_RANGE)
            .seatPreference(UPDATED_SEAT_PREFERENCE)
            .recommendedTrips(UPDATED_RECOMMENDED_TRIPS)
            .confidenceScore(UPDATED_CONFIDENCE_SCORE)
            .isBooked(UPDATED_IS_BOOKED)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            appUser = AppUserResourceIT.createUpdatedEntity();
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        updatedTripRecommendation.setUser(appUser);
        return updatedTripRecommendation;
    }

    @BeforeEach
    void initTest() {
        tripRecommendation = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTripRecommendation != null) {
            tripRecommendationRepository.delete(insertedTripRecommendation);
            insertedTripRecommendation = null;
        }
    }

    @Test
    @Transactional
    void createTripRecommendation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);
        var returnedTripRecommendationDTO = om.readValue(
            restTripRecommendationMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(tripRecommendationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TripRecommendationDTO.class
        );

        // Validate the TripRecommendation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTripRecommendation = tripRecommendationMapper.toEntity(returnedTripRecommendationDTO);
        assertTripRecommendationUpdatableFieldsEquals(
            returnedTripRecommendation,
            getPersistedTripRecommendation(returnedTripRecommendation)
        );

        insertedTripRecommendation = returnedTripRecommendation;
    }

    @Test
    @Transactional
    void createTripRecommendationWithExistingId() throws Exception {
        // Create the TripRecommendation with an existing ID
        tripRecommendation.setId(1L);
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOriginIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripRecommendation.setOrigin(null);

        // Create the TripRecommendation, which fails.
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        restTripRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDestinationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripRecommendation.setDestination(null);

        // Create the TripRecommendation, which fails.
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        restTripRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTravelDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripRecommendation.setTravelDate(null);

        // Create the TripRecommendation, which fails.
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        restTripRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripRecommendation.setCreatedAt(null);

        // Create the TripRecommendation, which fails.
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        restTripRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTripRecommendations() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList
        restTripRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tripRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].origin").value(hasItem(DEFAULT_ORIGIN)))
            .andExpect(jsonPath("$.[*].destination").value(hasItem(DEFAULT_DESTINATION)))
            .andExpect(jsonPath("$.[*].travelDate").value(hasItem(DEFAULT_TRAVEL_DATE.toString())))
            .andExpect(jsonPath("$.[*].preferredTime").value(hasItem(DEFAULT_PREFERRED_TIME.toString())))
            .andExpect(jsonPath("$.[*].budgetRange").value(hasItem(DEFAULT_BUDGET_RANGE)))
            .andExpect(jsonPath("$.[*].seatPreference").value(hasItem(DEFAULT_SEAT_PREFERENCE)))
            .andExpect(jsonPath("$.[*].recommendedTrips").value(hasItem(DEFAULT_RECOMMENDED_TRIPS)))
            .andExpect(jsonPath("$.[*].confidenceScore").value(hasItem(sameNumber(DEFAULT_CONFIDENCE_SCORE))))
            .andExpect(jsonPath("$.[*].isBooked").value(hasItem(DEFAULT_IS_BOOKED)))
            .andExpect(jsonPath("$.[*].feedbackRating").value(hasItem(DEFAULT_FEEDBACK_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getTripRecommendation() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get the tripRecommendation
        restTripRecommendationMockMvc
            .perform(get(ENTITY_API_URL_ID, tripRecommendation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tripRecommendation.getId().intValue()))
            .andExpect(jsonPath("$.origin").value(DEFAULT_ORIGIN))
            .andExpect(jsonPath("$.destination").value(DEFAULT_DESTINATION))
            .andExpect(jsonPath("$.travelDate").value(DEFAULT_TRAVEL_DATE.toString()))
            .andExpect(jsonPath("$.preferredTime").value(DEFAULT_PREFERRED_TIME.toString()))
            .andExpect(jsonPath("$.budgetRange").value(DEFAULT_BUDGET_RANGE))
            .andExpect(jsonPath("$.seatPreference").value(DEFAULT_SEAT_PREFERENCE))
            .andExpect(jsonPath("$.recommendedTrips").value(DEFAULT_RECOMMENDED_TRIPS))
            .andExpect(jsonPath("$.confidenceScore").value(sameNumber(DEFAULT_CONFIDENCE_SCORE)))
            .andExpect(jsonPath("$.isBooked").value(DEFAULT_IS_BOOKED))
            .andExpect(jsonPath("$.feedbackRating").value(DEFAULT_FEEDBACK_RATING))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getTripRecommendationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        Long id = tripRecommendation.getId();

        defaultTripRecommendationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTripRecommendationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTripRecommendationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByOriginIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where origin equals to
        defaultTripRecommendationFiltering("origin.equals=" + DEFAULT_ORIGIN, "origin.equals=" + UPDATED_ORIGIN);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByOriginIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where origin in
        defaultTripRecommendationFiltering("origin.in=" + DEFAULT_ORIGIN + "," + UPDATED_ORIGIN, "origin.in=" + UPDATED_ORIGIN);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByOriginIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where origin is not null
        defaultTripRecommendationFiltering("origin.specified=true", "origin.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByOriginContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where origin contains
        defaultTripRecommendationFiltering("origin.contains=" + DEFAULT_ORIGIN, "origin.contains=" + UPDATED_ORIGIN);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByOriginNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where origin does not contain
        defaultTripRecommendationFiltering("origin.doesNotContain=" + UPDATED_ORIGIN, "origin.doesNotContain=" + DEFAULT_ORIGIN);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDestinationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where destination equals to
        defaultTripRecommendationFiltering("destination.equals=" + DEFAULT_DESTINATION, "destination.equals=" + UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDestinationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where destination in
        defaultTripRecommendationFiltering(
            "destination.in=" + DEFAULT_DESTINATION + "," + UPDATED_DESTINATION,
            "destination.in=" + UPDATED_DESTINATION
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDestinationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where destination is not null
        defaultTripRecommendationFiltering("destination.specified=true", "destination.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDestinationContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where destination contains
        defaultTripRecommendationFiltering("destination.contains=" + DEFAULT_DESTINATION, "destination.contains=" + UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDestinationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where destination does not contain
        defaultTripRecommendationFiltering(
            "destination.doesNotContain=" + UPDATED_DESTINATION,
            "destination.doesNotContain=" + DEFAULT_DESTINATION
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate equals to
        defaultTripRecommendationFiltering("travelDate.equals=" + DEFAULT_TRAVEL_DATE, "travelDate.equals=" + UPDATED_TRAVEL_DATE);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate in
        defaultTripRecommendationFiltering(
            "travelDate.in=" + DEFAULT_TRAVEL_DATE + "," + UPDATED_TRAVEL_DATE,
            "travelDate.in=" + UPDATED_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate is not null
        defaultTripRecommendationFiltering("travelDate.specified=true", "travelDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate is greater than or equal to
        defaultTripRecommendationFiltering(
            "travelDate.greaterThanOrEqual=" + DEFAULT_TRAVEL_DATE,
            "travelDate.greaterThanOrEqual=" + UPDATED_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate is less than or equal to
        defaultTripRecommendationFiltering(
            "travelDate.lessThanOrEqual=" + DEFAULT_TRAVEL_DATE,
            "travelDate.lessThanOrEqual=" + SMALLER_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate is less than
        defaultTripRecommendationFiltering("travelDate.lessThan=" + UPDATED_TRAVEL_DATE, "travelDate.lessThan=" + DEFAULT_TRAVEL_DATE);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByTravelDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where travelDate is greater than
        defaultTripRecommendationFiltering(
            "travelDate.greaterThan=" + SMALLER_TRAVEL_DATE,
            "travelDate.greaterThan=" + DEFAULT_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByPreferredTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where preferredTime equals to
        defaultTripRecommendationFiltering(
            "preferredTime.equals=" + DEFAULT_PREFERRED_TIME,
            "preferredTime.equals=" + UPDATED_PREFERRED_TIME
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByPreferredTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where preferredTime in
        defaultTripRecommendationFiltering(
            "preferredTime.in=" + DEFAULT_PREFERRED_TIME + "," + UPDATED_PREFERRED_TIME,
            "preferredTime.in=" + UPDATED_PREFERRED_TIME
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByPreferredTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where preferredTime is not null
        defaultTripRecommendationFiltering("preferredTime.specified=true", "preferredTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByBudgetRangeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where budgetRange equals to
        defaultTripRecommendationFiltering("budgetRange.equals=" + DEFAULT_BUDGET_RANGE, "budgetRange.equals=" + UPDATED_BUDGET_RANGE);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByBudgetRangeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where budgetRange in
        defaultTripRecommendationFiltering(
            "budgetRange.in=" + DEFAULT_BUDGET_RANGE + "," + UPDATED_BUDGET_RANGE,
            "budgetRange.in=" + UPDATED_BUDGET_RANGE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByBudgetRangeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where budgetRange is not null
        defaultTripRecommendationFiltering("budgetRange.specified=true", "budgetRange.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByBudgetRangeContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where budgetRange contains
        defaultTripRecommendationFiltering("budgetRange.contains=" + DEFAULT_BUDGET_RANGE, "budgetRange.contains=" + UPDATED_BUDGET_RANGE);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByBudgetRangeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where budgetRange does not contain
        defaultTripRecommendationFiltering(
            "budgetRange.doesNotContain=" + UPDATED_BUDGET_RANGE,
            "budgetRange.doesNotContain=" + DEFAULT_BUDGET_RANGE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsBySeatPreferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where seatPreference equals to
        defaultTripRecommendationFiltering(
            "seatPreference.equals=" + DEFAULT_SEAT_PREFERENCE,
            "seatPreference.equals=" + UPDATED_SEAT_PREFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsBySeatPreferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where seatPreference in
        defaultTripRecommendationFiltering(
            "seatPreference.in=" + DEFAULT_SEAT_PREFERENCE + "," + UPDATED_SEAT_PREFERENCE,
            "seatPreference.in=" + UPDATED_SEAT_PREFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsBySeatPreferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where seatPreference is not null
        defaultTripRecommendationFiltering("seatPreference.specified=true", "seatPreference.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsBySeatPreferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where seatPreference contains
        defaultTripRecommendationFiltering(
            "seatPreference.contains=" + DEFAULT_SEAT_PREFERENCE,
            "seatPreference.contains=" + UPDATED_SEAT_PREFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsBySeatPreferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where seatPreference does not contain
        defaultTripRecommendationFiltering(
            "seatPreference.doesNotContain=" + UPDATED_SEAT_PREFERENCE,
            "seatPreference.doesNotContain=" + DEFAULT_SEAT_PREFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByRecommendedTripsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where recommendedTrips equals to
        defaultTripRecommendationFiltering(
            "recommendedTrips.equals=" + DEFAULT_RECOMMENDED_TRIPS,
            "recommendedTrips.equals=" + UPDATED_RECOMMENDED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByRecommendedTripsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where recommendedTrips in
        defaultTripRecommendationFiltering(
            "recommendedTrips.in=" + DEFAULT_RECOMMENDED_TRIPS + "," + UPDATED_RECOMMENDED_TRIPS,
            "recommendedTrips.in=" + UPDATED_RECOMMENDED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByRecommendedTripsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where recommendedTrips is not null
        defaultTripRecommendationFiltering("recommendedTrips.specified=true", "recommendedTrips.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByRecommendedTripsContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where recommendedTrips contains
        defaultTripRecommendationFiltering(
            "recommendedTrips.contains=" + DEFAULT_RECOMMENDED_TRIPS,
            "recommendedTrips.contains=" + UPDATED_RECOMMENDED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByRecommendedTripsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where recommendedTrips does not contain
        defaultTripRecommendationFiltering(
            "recommendedTrips.doesNotContain=" + UPDATED_RECOMMENDED_TRIPS,
            "recommendedTrips.doesNotContain=" + DEFAULT_RECOMMENDED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore equals to
        defaultTripRecommendationFiltering(
            "confidenceScore.equals=" + DEFAULT_CONFIDENCE_SCORE,
            "confidenceScore.equals=" + UPDATED_CONFIDENCE_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore in
        defaultTripRecommendationFiltering(
            "confidenceScore.in=" + DEFAULT_CONFIDENCE_SCORE + "," + UPDATED_CONFIDENCE_SCORE,
            "confidenceScore.in=" + UPDATED_CONFIDENCE_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore is not null
        defaultTripRecommendationFiltering("confidenceScore.specified=true", "confidenceScore.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore is greater than or equal to
        defaultTripRecommendationFiltering(
            "confidenceScore.greaterThanOrEqual=" + DEFAULT_CONFIDENCE_SCORE,
            "confidenceScore.greaterThanOrEqual=" + UPDATED_CONFIDENCE_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore is less than or equal to
        defaultTripRecommendationFiltering(
            "confidenceScore.lessThanOrEqual=" + DEFAULT_CONFIDENCE_SCORE,
            "confidenceScore.lessThanOrEqual=" + SMALLER_CONFIDENCE_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore is less than
        defaultTripRecommendationFiltering(
            "confidenceScore.lessThan=" + UPDATED_CONFIDENCE_SCORE,
            "confidenceScore.lessThan=" + DEFAULT_CONFIDENCE_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByConfidenceScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where confidenceScore is greater than
        defaultTripRecommendationFiltering(
            "confidenceScore.greaterThan=" + SMALLER_CONFIDENCE_SCORE,
            "confidenceScore.greaterThan=" + DEFAULT_CONFIDENCE_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByIsBookedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where isBooked equals to
        defaultTripRecommendationFiltering("isBooked.equals=" + DEFAULT_IS_BOOKED, "isBooked.equals=" + UPDATED_IS_BOOKED);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByIsBookedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where isBooked in
        defaultTripRecommendationFiltering(
            "isBooked.in=" + DEFAULT_IS_BOOKED + "," + UPDATED_IS_BOOKED,
            "isBooked.in=" + UPDATED_IS_BOOKED
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByIsBookedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where isBooked is not null
        defaultTripRecommendationFiltering("isBooked.specified=true", "isBooked.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating equals to
        defaultTripRecommendationFiltering(
            "feedbackRating.equals=" + DEFAULT_FEEDBACK_RATING,
            "feedbackRating.equals=" + UPDATED_FEEDBACK_RATING
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating in
        defaultTripRecommendationFiltering(
            "feedbackRating.in=" + DEFAULT_FEEDBACK_RATING + "," + UPDATED_FEEDBACK_RATING,
            "feedbackRating.in=" + UPDATED_FEEDBACK_RATING
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating is not null
        defaultTripRecommendationFiltering("feedbackRating.specified=true", "feedbackRating.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating is greater than or equal to
        defaultTripRecommendationFiltering(
            "feedbackRating.greaterThanOrEqual=" + DEFAULT_FEEDBACK_RATING,
            "feedbackRating.greaterThanOrEqual=" + UPDATED_FEEDBACK_RATING
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating is less than or equal to
        defaultTripRecommendationFiltering(
            "feedbackRating.lessThanOrEqual=" + DEFAULT_FEEDBACK_RATING,
            "feedbackRating.lessThanOrEqual=" + SMALLER_FEEDBACK_RATING
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating is less than
        defaultTripRecommendationFiltering(
            "feedbackRating.lessThan=" + UPDATED_FEEDBACK_RATING,
            "feedbackRating.lessThan=" + DEFAULT_FEEDBACK_RATING
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByFeedbackRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where feedbackRating is greater than
        defaultTripRecommendationFiltering(
            "feedbackRating.greaterThan=" + SMALLER_FEEDBACK_RATING,
            "feedbackRating.greaterThan=" + DEFAULT_FEEDBACK_RATING
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where createdAt equals to
        defaultTripRecommendationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where createdAt in
        defaultTripRecommendationFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where createdAt is not null
        defaultTripRecommendationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where updatedAt equals to
        defaultTripRecommendationFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where updatedAt in
        defaultTripRecommendationFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where updatedAt is not null
        defaultTripRecommendationFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where isDeleted equals to
        defaultTripRecommendationFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where isDeleted in
        defaultTripRecommendationFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where isDeleted is not null
        defaultTripRecommendationFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where deletedAt equals to
        defaultTripRecommendationFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where deletedAt in
        defaultTripRecommendationFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where deletedAt is not null
        defaultTripRecommendationFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where deletedBy equals to
        defaultTripRecommendationFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where deletedBy in
        defaultTripRecommendationFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        // Get all the tripRecommendationList where deletedBy is not null
        defaultTripRecommendationFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTripRecommendationsByUserIsEqualToSomething() throws Exception {
        AppUser user;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            tripRecommendationRepository.saveAndFlush(tripRecommendation);
            user = AppUserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(user);
        em.flush();
        tripRecommendation.setUser(user);
        tripRecommendationRepository.saveAndFlush(tripRecommendation);
        Long userId = user.getId();
        // Get all the tripRecommendationList where user equals to userId
        defaultTripRecommendationShouldBeFound("userId.equals=" + userId);

        // Get all the tripRecommendationList where user equals to (userId + 1)
        defaultTripRecommendationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultTripRecommendationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTripRecommendationShouldBeFound(shouldBeFound);
        defaultTripRecommendationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTripRecommendationShouldBeFound(String filter) throws Exception {
        restTripRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tripRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].origin").value(hasItem(DEFAULT_ORIGIN)))
            .andExpect(jsonPath("$.[*].destination").value(hasItem(DEFAULT_DESTINATION)))
            .andExpect(jsonPath("$.[*].travelDate").value(hasItem(DEFAULT_TRAVEL_DATE.toString())))
            .andExpect(jsonPath("$.[*].preferredTime").value(hasItem(DEFAULT_PREFERRED_TIME.toString())))
            .andExpect(jsonPath("$.[*].budgetRange").value(hasItem(DEFAULT_BUDGET_RANGE)))
            .andExpect(jsonPath("$.[*].seatPreference").value(hasItem(DEFAULT_SEAT_PREFERENCE)))
            .andExpect(jsonPath("$.[*].recommendedTrips").value(hasItem(DEFAULT_RECOMMENDED_TRIPS)))
            .andExpect(jsonPath("$.[*].confidenceScore").value(hasItem(sameNumber(DEFAULT_CONFIDENCE_SCORE))))
            .andExpect(jsonPath("$.[*].isBooked").value(hasItem(DEFAULT_IS_BOOKED)))
            .andExpect(jsonPath("$.[*].feedbackRating").value(hasItem(DEFAULT_FEEDBACK_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restTripRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTripRecommendationShouldNotBeFound(String filter) throws Exception {
        restTripRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTripRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTripRecommendation() throws Exception {
        // Get the tripRecommendation
        restTripRecommendationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTripRecommendation() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tripRecommendation
        TripRecommendation updatedTripRecommendation = tripRecommendationRepository.findById(tripRecommendation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTripRecommendation are not directly saved in db
        em.detach(updatedTripRecommendation);
        updatedTripRecommendation
            .origin(UPDATED_ORIGIN)
            .destination(UPDATED_DESTINATION)
            .travelDate(UPDATED_TRAVEL_DATE)
            .preferredTime(UPDATED_PREFERRED_TIME)
            .budgetRange(UPDATED_BUDGET_RANGE)
            .seatPreference(UPDATED_SEAT_PREFERENCE)
            .recommendedTrips(UPDATED_RECOMMENDED_TRIPS)
            .confidenceScore(UPDATED_CONFIDENCE_SCORE)
            .isBooked(UPDATED_IS_BOOKED)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(updatedTripRecommendation);

        restTripRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripRecommendationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isOk());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTripRecommendationToMatchAllProperties(updatedTripRecommendation);
    }

    @Test
    @Transactional
    void putNonExistingTripRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripRecommendation.setId(longCount.incrementAndGet());

        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripRecommendationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTripRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripRecommendation.setId(longCount.incrementAndGet());

        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTripRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripRecommendation.setId(longCount.incrementAndGet());

        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTripRecommendationWithPatch() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tripRecommendation using partial update
        TripRecommendation partialUpdatedTripRecommendation = new TripRecommendation();
        partialUpdatedTripRecommendation.setId(tripRecommendation.getId());

        partialUpdatedTripRecommendation
            .destination(UPDATED_DESTINATION)
            .travelDate(UPDATED_TRAVEL_DATE)
            .budgetRange(UPDATED_BUDGET_RANGE)
            .seatPreference(UPDATED_SEAT_PREFERENCE)
            .recommendedTrips(UPDATED_RECOMMENDED_TRIPS)
            .confidenceScore(UPDATED_CONFIDENCE_SCORE)
            .isBooked(UPDATED_IS_BOOKED)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restTripRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTripRecommendation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTripRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the TripRecommendation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripRecommendationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTripRecommendation, tripRecommendation),
            getPersistedTripRecommendation(tripRecommendation)
        );
    }

    @Test
    @Transactional
    void fullUpdateTripRecommendationWithPatch() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tripRecommendation using partial update
        TripRecommendation partialUpdatedTripRecommendation = new TripRecommendation();
        partialUpdatedTripRecommendation.setId(tripRecommendation.getId());

        partialUpdatedTripRecommendation
            .origin(UPDATED_ORIGIN)
            .destination(UPDATED_DESTINATION)
            .travelDate(UPDATED_TRAVEL_DATE)
            .preferredTime(UPDATED_PREFERRED_TIME)
            .budgetRange(UPDATED_BUDGET_RANGE)
            .seatPreference(UPDATED_SEAT_PREFERENCE)
            .recommendedTrips(UPDATED_RECOMMENDED_TRIPS)
            .confidenceScore(UPDATED_CONFIDENCE_SCORE)
            .isBooked(UPDATED_IS_BOOKED)
            .feedbackRating(UPDATED_FEEDBACK_RATING)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restTripRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTripRecommendation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTripRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the TripRecommendation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripRecommendationUpdatableFieldsEquals(
            partialUpdatedTripRecommendation,
            getPersistedTripRecommendation(partialUpdatedTripRecommendation)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTripRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripRecommendation.setId(longCount.incrementAndGet());

        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tripRecommendationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTripRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripRecommendation.setId(longCount.incrementAndGet());

        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTripRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripRecommendation.setId(longCount.incrementAndGet());

        // Create the TripRecommendation
        TripRecommendationDTO tripRecommendationDTO = tripRecommendationMapper.toDto(tripRecommendation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripRecommendationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TripRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTripRecommendation() throws Exception {
        // Initialize the database
        insertedTripRecommendation = tripRecommendationRepository.saveAndFlush(tripRecommendation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tripRecommendation
        restTripRecommendationMockMvc
            .perform(delete(ENTITY_API_URL_ID, tripRecommendation.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tripRecommendationRepository.count();
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

    protected TripRecommendation getPersistedTripRecommendation(TripRecommendation tripRecommendation) {
        return tripRecommendationRepository.findById(tripRecommendation.getId()).orElseThrow();
    }

    protected void assertPersistedTripRecommendationToMatchAllProperties(TripRecommendation expectedTripRecommendation) {
        assertTripRecommendationAllPropertiesEquals(expectedTripRecommendation, getPersistedTripRecommendation(expectedTripRecommendation));
    }

    protected void assertPersistedTripRecommendationToMatchUpdatableProperties(TripRecommendation expectedTripRecommendation) {
        assertTripRecommendationAllUpdatablePropertiesEquals(
            expectedTripRecommendation,
            getPersistedTripRecommendation(expectedTripRecommendation)
        );
    }
}
