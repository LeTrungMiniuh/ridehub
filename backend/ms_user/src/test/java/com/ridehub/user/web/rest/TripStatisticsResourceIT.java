package com.ridehub.user.web.rest;

import static com.ridehub.user.domain.TripStatisticsAsserts.*;
import static com.ridehub.user.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.user.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.user.IntegrationTest;
import com.ridehub.user.domain.TripStatistics;
import com.ridehub.user.domain.enumeration.OccasionType;
import com.ridehub.user.domain.enumeration.VehicleType;
import com.ridehub.user.repository.TripStatisticsRepository;
import com.ridehub.user.service.dto.TripStatisticsDTO;
import com.ridehub.user.service.mapper.TripStatisticsMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
 * Integration tests for the {@link TripStatisticsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TripStatisticsResourceIT {

    private static final Long DEFAULT_ROUTE_ID = 1L;
    private static final Long UPDATED_ROUTE_ID = 2L;
    private static final Long SMALLER_ROUTE_ID = 1L - 1L;

    private static final VehicleType DEFAULT_VEHICLE_TYPE = VehicleType.STANDARD_BUS_VIP;
    private static final VehicleType UPDATED_VEHICLE_TYPE = VehicleType.STANDARD_BUS_NORMAL;

    private static final OccasionType DEFAULT_OCCASION_TYPE = OccasionType.NORMAL;
    private static final OccasionType UPDATED_OCCASION_TYPE = OccasionType.WEEKEND;

    private static final Integer DEFAULT_TOTAL_BOOKINGS = 1;
    private static final Integer UPDATED_TOTAL_BOOKINGS = 2;
    private static final Integer SMALLER_TOTAL_BOOKINGS = 1 - 1;

    private static final BigDecimal DEFAULT_TOTAL_REVENUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_REVENUE = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_REVENUE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_AVERAGE_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVERAGE_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_AVERAGE_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_OCCUPANCY_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OCCUPANCY_RATE = new BigDecimal(2);
    private static final BigDecimal SMALLER_OCCUPANCY_RATE = new BigDecimal(1 - 1);

    private static final String DEFAULT_POPULAR_SEAT_TYPES = "AAAAAAAAAA";
    private static final String UPDATED_POPULAR_SEAT_TYPES = "BBBBBBBBBB";

    private static final String DEFAULT_PEAK_TRAVEL_TIMES = "AAAAAAAAAA";
    private static final String UPDATED_PEAK_TRAVEL_TIMES = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_CANCELLATION_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CANCELLATION_RATE = new BigDecimal(2);
    private static final BigDecimal SMALLER_CANCELLATION_RATE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_CUSTOMER_SATISFACTION_SCORE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CUSTOMER_SATISFACTION_SCORE = new BigDecimal(2);
    private static final BigDecimal SMALLER_CUSTOMER_SATISFACTION_SCORE = new BigDecimal(1 - 1);

    private static final String DEFAULT_MONTHLY_TREND = "AAAAAAAAAA";
    private static final String UPDATED_MONTHLY_TREND = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_VALID_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALID_FROM = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_VALID_FROM = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_VALID_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALID_TO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_VALID_TO = LocalDate.ofEpochDay(-1L);

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

    private static final String ENTITY_API_URL = "/api/trip-statistics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TripStatisticsRepository tripStatisticsRepository;

    @Autowired
    private TripStatisticsMapper tripStatisticsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTripStatisticsMockMvc;

    private TripStatistics tripStatistics;

    private TripStatistics insertedTripStatistics;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TripStatistics createEntity() {
        return new TripStatistics()
            .routeId(DEFAULT_ROUTE_ID)
            .vehicleType(DEFAULT_VEHICLE_TYPE)
            .occasionType(DEFAULT_OCCASION_TYPE)
            .totalBookings(DEFAULT_TOTAL_BOOKINGS)
            .totalRevenue(DEFAULT_TOTAL_REVENUE)
            .averagePrice(DEFAULT_AVERAGE_PRICE)
            .occupancyRate(DEFAULT_OCCUPANCY_RATE)
            .popularSeatTypes(DEFAULT_POPULAR_SEAT_TYPES)
            .peakTravelTimes(DEFAULT_PEAK_TRAVEL_TIMES)
            .cancellationRate(DEFAULT_CANCELLATION_RATE)
            .customerSatisfactionScore(DEFAULT_CUSTOMER_SATISFACTION_SCORE)
            .monthlyTrend(DEFAULT_MONTHLY_TREND)
            .validFrom(DEFAULT_VALID_FROM)
            .validTo(DEFAULT_VALID_TO)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TripStatistics createUpdatedEntity() {
        return new TripStatistics()
            .routeId(UPDATED_ROUTE_ID)
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .totalBookings(UPDATED_TOTAL_BOOKINGS)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .averagePrice(UPDATED_AVERAGE_PRICE)
            .occupancyRate(UPDATED_OCCUPANCY_RATE)
            .popularSeatTypes(UPDATED_POPULAR_SEAT_TYPES)
            .peakTravelTimes(UPDATED_PEAK_TRAVEL_TIMES)
            .cancellationRate(UPDATED_CANCELLATION_RATE)
            .customerSatisfactionScore(UPDATED_CUSTOMER_SATISFACTION_SCORE)
            .monthlyTrend(UPDATED_MONTHLY_TREND)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        tripStatistics = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTripStatistics != null) {
            tripStatisticsRepository.delete(insertedTripStatistics);
            insertedTripStatistics = null;
        }
    }

    @Test
    @Transactional
    void createTripStatistics() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);
        var returnedTripStatisticsDTO = om.readValue(
            restTripStatisticsMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(tripStatisticsDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TripStatisticsDTO.class
        );

        // Validate the TripStatistics in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTripStatistics = tripStatisticsMapper.toEntity(returnedTripStatisticsDTO);
        assertTripStatisticsUpdatableFieldsEquals(returnedTripStatistics, getPersistedTripStatistics(returnedTripStatistics));

        insertedTripStatistics = returnedTripStatistics;
    }

    @Test
    @Transactional
    void createTripStatisticsWithExistingId() throws Exception {
        // Create the TripStatistics with an existing ID
        tripStatistics.setId(1L);
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRouteIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripStatistics.setRouteId(null);

        // Create the TripStatistics, which fails.
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        restTripStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVehicleTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripStatistics.setVehicleType(null);

        // Create the TripStatistics, which fails.
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        restTripStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOccasionTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripStatistics.setOccasionType(null);

        // Create the TripStatistics, which fails.
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        restTripStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripStatistics.setValidFrom(null);

        // Create the TripStatistics, which fails.
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        restTripStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tripStatistics.setCreatedAt(null);

        // Create the TripStatistics, which fails.
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        restTripStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTripStatistics() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList
        restTripStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tripStatistics.getId().intValue())))
            .andExpect(jsonPath("$.[*].routeId").value(hasItem(DEFAULT_ROUTE_ID.intValue())))
            .andExpect(jsonPath("$.[*].vehicleType").value(hasItem(DEFAULT_VEHICLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].occasionType").value(hasItem(DEFAULT_OCCASION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].totalBookings").value(hasItem(DEFAULT_TOTAL_BOOKINGS)))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(sameNumber(DEFAULT_TOTAL_REVENUE))))
            .andExpect(jsonPath("$.[*].averagePrice").value(hasItem(sameNumber(DEFAULT_AVERAGE_PRICE))))
            .andExpect(jsonPath("$.[*].occupancyRate").value(hasItem(sameNumber(DEFAULT_OCCUPANCY_RATE))))
            .andExpect(jsonPath("$.[*].popularSeatTypes").value(hasItem(DEFAULT_POPULAR_SEAT_TYPES)))
            .andExpect(jsonPath("$.[*].peakTravelTimes").value(hasItem(DEFAULT_PEAK_TRAVEL_TIMES)))
            .andExpect(jsonPath("$.[*].cancellationRate").value(hasItem(sameNumber(DEFAULT_CANCELLATION_RATE))))
            .andExpect(jsonPath("$.[*].customerSatisfactionScore").value(hasItem(sameNumber(DEFAULT_CUSTOMER_SATISFACTION_SCORE))))
            .andExpect(jsonPath("$.[*].monthlyTrend").value(hasItem(DEFAULT_MONTHLY_TREND)))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getTripStatistics() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get the tripStatistics
        restTripStatisticsMockMvc
            .perform(get(ENTITY_API_URL_ID, tripStatistics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tripStatistics.getId().intValue()))
            .andExpect(jsonPath("$.routeId").value(DEFAULT_ROUTE_ID.intValue()))
            .andExpect(jsonPath("$.vehicleType").value(DEFAULT_VEHICLE_TYPE.toString()))
            .andExpect(jsonPath("$.occasionType").value(DEFAULT_OCCASION_TYPE.toString()))
            .andExpect(jsonPath("$.totalBookings").value(DEFAULT_TOTAL_BOOKINGS))
            .andExpect(jsonPath("$.totalRevenue").value(sameNumber(DEFAULT_TOTAL_REVENUE)))
            .andExpect(jsonPath("$.averagePrice").value(sameNumber(DEFAULT_AVERAGE_PRICE)))
            .andExpect(jsonPath("$.occupancyRate").value(sameNumber(DEFAULT_OCCUPANCY_RATE)))
            .andExpect(jsonPath("$.popularSeatTypes").value(DEFAULT_POPULAR_SEAT_TYPES))
            .andExpect(jsonPath("$.peakTravelTimes").value(DEFAULT_PEAK_TRAVEL_TIMES))
            .andExpect(jsonPath("$.cancellationRate").value(sameNumber(DEFAULT_CANCELLATION_RATE)))
            .andExpect(jsonPath("$.customerSatisfactionScore").value(sameNumber(DEFAULT_CUSTOMER_SATISFACTION_SCORE)))
            .andExpect(jsonPath("$.monthlyTrend").value(DEFAULT_MONTHLY_TREND))
            .andExpect(jsonPath("$.validFrom").value(DEFAULT_VALID_FROM.toString()))
            .andExpect(jsonPath("$.validTo").value(DEFAULT_VALID_TO.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getTripStatisticsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        Long id = tripStatistics.getId();

        defaultTripStatisticsFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTripStatisticsFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTripStatisticsFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId equals to
        defaultTripStatisticsFiltering("routeId.equals=" + DEFAULT_ROUTE_ID, "routeId.equals=" + UPDATED_ROUTE_ID);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId in
        defaultTripStatisticsFiltering("routeId.in=" + DEFAULT_ROUTE_ID + "," + UPDATED_ROUTE_ID, "routeId.in=" + UPDATED_ROUTE_ID);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId is not null
        defaultTripStatisticsFiltering("routeId.specified=true", "routeId.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId is greater than or equal to
        defaultTripStatisticsFiltering("routeId.greaterThanOrEqual=" + DEFAULT_ROUTE_ID, "routeId.greaterThanOrEqual=" + UPDATED_ROUTE_ID);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId is less than or equal to
        defaultTripStatisticsFiltering("routeId.lessThanOrEqual=" + DEFAULT_ROUTE_ID, "routeId.lessThanOrEqual=" + SMALLER_ROUTE_ID);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId is less than
        defaultTripStatisticsFiltering("routeId.lessThan=" + UPDATED_ROUTE_ID, "routeId.lessThan=" + DEFAULT_ROUTE_ID);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByRouteIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where routeId is greater than
        defaultTripStatisticsFiltering("routeId.greaterThan=" + SMALLER_ROUTE_ID, "routeId.greaterThan=" + DEFAULT_ROUTE_ID);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByVehicleTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where vehicleType equals to
        defaultTripStatisticsFiltering("vehicleType.equals=" + DEFAULT_VEHICLE_TYPE, "vehicleType.equals=" + UPDATED_VEHICLE_TYPE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByVehicleTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where vehicleType in
        defaultTripStatisticsFiltering(
            "vehicleType.in=" + DEFAULT_VEHICLE_TYPE + "," + UPDATED_VEHICLE_TYPE,
            "vehicleType.in=" + UPDATED_VEHICLE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByVehicleTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where vehicleType is not null
        defaultTripStatisticsFiltering("vehicleType.specified=true", "vehicleType.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccasionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occasionType equals to
        defaultTripStatisticsFiltering("occasionType.equals=" + DEFAULT_OCCASION_TYPE, "occasionType.equals=" + UPDATED_OCCASION_TYPE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccasionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occasionType in
        defaultTripStatisticsFiltering(
            "occasionType.in=" + DEFAULT_OCCASION_TYPE + "," + UPDATED_OCCASION_TYPE,
            "occasionType.in=" + UPDATED_OCCASION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccasionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occasionType is not null
        defaultTripStatisticsFiltering("occasionType.specified=true", "occasionType.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings equals to
        defaultTripStatisticsFiltering("totalBookings.equals=" + DEFAULT_TOTAL_BOOKINGS, "totalBookings.equals=" + UPDATED_TOTAL_BOOKINGS);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings in
        defaultTripStatisticsFiltering(
            "totalBookings.in=" + DEFAULT_TOTAL_BOOKINGS + "," + UPDATED_TOTAL_BOOKINGS,
            "totalBookings.in=" + UPDATED_TOTAL_BOOKINGS
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings is not null
        defaultTripStatisticsFiltering("totalBookings.specified=true", "totalBookings.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings is greater than or equal to
        defaultTripStatisticsFiltering(
            "totalBookings.greaterThanOrEqual=" + DEFAULT_TOTAL_BOOKINGS,
            "totalBookings.greaterThanOrEqual=" + UPDATED_TOTAL_BOOKINGS
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings is less than or equal to
        defaultTripStatisticsFiltering(
            "totalBookings.lessThanOrEqual=" + DEFAULT_TOTAL_BOOKINGS,
            "totalBookings.lessThanOrEqual=" + SMALLER_TOTAL_BOOKINGS
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings is less than
        defaultTripStatisticsFiltering(
            "totalBookings.lessThan=" + UPDATED_TOTAL_BOOKINGS,
            "totalBookings.lessThan=" + DEFAULT_TOTAL_BOOKINGS
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalBookingsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalBookings is greater than
        defaultTripStatisticsFiltering(
            "totalBookings.greaterThan=" + SMALLER_TOTAL_BOOKINGS,
            "totalBookings.greaterThan=" + DEFAULT_TOTAL_BOOKINGS
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue equals to
        defaultTripStatisticsFiltering("totalRevenue.equals=" + DEFAULT_TOTAL_REVENUE, "totalRevenue.equals=" + UPDATED_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue in
        defaultTripStatisticsFiltering(
            "totalRevenue.in=" + DEFAULT_TOTAL_REVENUE + "," + UPDATED_TOTAL_REVENUE,
            "totalRevenue.in=" + UPDATED_TOTAL_REVENUE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue is not null
        defaultTripStatisticsFiltering("totalRevenue.specified=true", "totalRevenue.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue is greater than or equal to
        defaultTripStatisticsFiltering(
            "totalRevenue.greaterThanOrEqual=" + DEFAULT_TOTAL_REVENUE,
            "totalRevenue.greaterThanOrEqual=" + UPDATED_TOTAL_REVENUE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue is less than or equal to
        defaultTripStatisticsFiltering(
            "totalRevenue.lessThanOrEqual=" + DEFAULT_TOTAL_REVENUE,
            "totalRevenue.lessThanOrEqual=" + SMALLER_TOTAL_REVENUE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue is less than
        defaultTripStatisticsFiltering("totalRevenue.lessThan=" + UPDATED_TOTAL_REVENUE, "totalRevenue.lessThan=" + DEFAULT_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByTotalRevenueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where totalRevenue is greater than
        defaultTripStatisticsFiltering(
            "totalRevenue.greaterThan=" + SMALLER_TOTAL_REVENUE,
            "totalRevenue.greaterThan=" + DEFAULT_TOTAL_REVENUE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice equals to
        defaultTripStatisticsFiltering("averagePrice.equals=" + DEFAULT_AVERAGE_PRICE, "averagePrice.equals=" + UPDATED_AVERAGE_PRICE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice in
        defaultTripStatisticsFiltering(
            "averagePrice.in=" + DEFAULT_AVERAGE_PRICE + "," + UPDATED_AVERAGE_PRICE,
            "averagePrice.in=" + UPDATED_AVERAGE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice is not null
        defaultTripStatisticsFiltering("averagePrice.specified=true", "averagePrice.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice is greater than or equal to
        defaultTripStatisticsFiltering(
            "averagePrice.greaterThanOrEqual=" + DEFAULT_AVERAGE_PRICE,
            "averagePrice.greaterThanOrEqual=" + UPDATED_AVERAGE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice is less than or equal to
        defaultTripStatisticsFiltering(
            "averagePrice.lessThanOrEqual=" + DEFAULT_AVERAGE_PRICE,
            "averagePrice.lessThanOrEqual=" + SMALLER_AVERAGE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice is less than
        defaultTripStatisticsFiltering("averagePrice.lessThan=" + UPDATED_AVERAGE_PRICE, "averagePrice.lessThan=" + DEFAULT_AVERAGE_PRICE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByAveragePriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where averagePrice is greater than
        defaultTripStatisticsFiltering(
            "averagePrice.greaterThan=" + SMALLER_AVERAGE_PRICE,
            "averagePrice.greaterThan=" + DEFAULT_AVERAGE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate equals to
        defaultTripStatisticsFiltering("occupancyRate.equals=" + DEFAULT_OCCUPANCY_RATE, "occupancyRate.equals=" + UPDATED_OCCUPANCY_RATE);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate in
        defaultTripStatisticsFiltering(
            "occupancyRate.in=" + DEFAULT_OCCUPANCY_RATE + "," + UPDATED_OCCUPANCY_RATE,
            "occupancyRate.in=" + UPDATED_OCCUPANCY_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate is not null
        defaultTripStatisticsFiltering("occupancyRate.specified=true", "occupancyRate.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate is greater than or equal to
        defaultTripStatisticsFiltering(
            "occupancyRate.greaterThanOrEqual=" + DEFAULT_OCCUPANCY_RATE,
            "occupancyRate.greaterThanOrEqual=" + UPDATED_OCCUPANCY_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate is less than or equal to
        defaultTripStatisticsFiltering(
            "occupancyRate.lessThanOrEqual=" + DEFAULT_OCCUPANCY_RATE,
            "occupancyRate.lessThanOrEqual=" + SMALLER_OCCUPANCY_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate is less than
        defaultTripStatisticsFiltering(
            "occupancyRate.lessThan=" + UPDATED_OCCUPANCY_RATE,
            "occupancyRate.lessThan=" + DEFAULT_OCCUPANCY_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByOccupancyRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where occupancyRate is greater than
        defaultTripStatisticsFiltering(
            "occupancyRate.greaterThan=" + SMALLER_OCCUPANCY_RATE,
            "occupancyRate.greaterThan=" + DEFAULT_OCCUPANCY_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPopularSeatTypesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where popularSeatTypes equals to
        defaultTripStatisticsFiltering(
            "popularSeatTypes.equals=" + DEFAULT_POPULAR_SEAT_TYPES,
            "popularSeatTypes.equals=" + UPDATED_POPULAR_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPopularSeatTypesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where popularSeatTypes in
        defaultTripStatisticsFiltering(
            "popularSeatTypes.in=" + DEFAULT_POPULAR_SEAT_TYPES + "," + UPDATED_POPULAR_SEAT_TYPES,
            "popularSeatTypes.in=" + UPDATED_POPULAR_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPopularSeatTypesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where popularSeatTypes is not null
        defaultTripStatisticsFiltering("popularSeatTypes.specified=true", "popularSeatTypes.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPopularSeatTypesContainsSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where popularSeatTypes contains
        defaultTripStatisticsFiltering(
            "popularSeatTypes.contains=" + DEFAULT_POPULAR_SEAT_TYPES,
            "popularSeatTypes.contains=" + UPDATED_POPULAR_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPopularSeatTypesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where popularSeatTypes does not contain
        defaultTripStatisticsFiltering(
            "popularSeatTypes.doesNotContain=" + UPDATED_POPULAR_SEAT_TYPES,
            "popularSeatTypes.doesNotContain=" + DEFAULT_POPULAR_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPeakTravelTimesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where peakTravelTimes equals to
        defaultTripStatisticsFiltering(
            "peakTravelTimes.equals=" + DEFAULT_PEAK_TRAVEL_TIMES,
            "peakTravelTimes.equals=" + UPDATED_PEAK_TRAVEL_TIMES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPeakTravelTimesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where peakTravelTimes in
        defaultTripStatisticsFiltering(
            "peakTravelTimes.in=" + DEFAULT_PEAK_TRAVEL_TIMES + "," + UPDATED_PEAK_TRAVEL_TIMES,
            "peakTravelTimes.in=" + UPDATED_PEAK_TRAVEL_TIMES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPeakTravelTimesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where peakTravelTimes is not null
        defaultTripStatisticsFiltering("peakTravelTimes.specified=true", "peakTravelTimes.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPeakTravelTimesContainsSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where peakTravelTimes contains
        defaultTripStatisticsFiltering(
            "peakTravelTimes.contains=" + DEFAULT_PEAK_TRAVEL_TIMES,
            "peakTravelTimes.contains=" + UPDATED_PEAK_TRAVEL_TIMES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByPeakTravelTimesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where peakTravelTimes does not contain
        defaultTripStatisticsFiltering(
            "peakTravelTimes.doesNotContain=" + UPDATED_PEAK_TRAVEL_TIMES,
            "peakTravelTimes.doesNotContain=" + DEFAULT_PEAK_TRAVEL_TIMES
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate equals to
        defaultTripStatisticsFiltering(
            "cancellationRate.equals=" + DEFAULT_CANCELLATION_RATE,
            "cancellationRate.equals=" + UPDATED_CANCELLATION_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate in
        defaultTripStatisticsFiltering(
            "cancellationRate.in=" + DEFAULT_CANCELLATION_RATE + "," + UPDATED_CANCELLATION_RATE,
            "cancellationRate.in=" + UPDATED_CANCELLATION_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate is not null
        defaultTripStatisticsFiltering("cancellationRate.specified=true", "cancellationRate.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate is greater than or equal to
        defaultTripStatisticsFiltering(
            "cancellationRate.greaterThanOrEqual=" + DEFAULT_CANCELLATION_RATE,
            "cancellationRate.greaterThanOrEqual=" + UPDATED_CANCELLATION_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate is less than or equal to
        defaultTripStatisticsFiltering(
            "cancellationRate.lessThanOrEqual=" + DEFAULT_CANCELLATION_RATE,
            "cancellationRate.lessThanOrEqual=" + SMALLER_CANCELLATION_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate is less than
        defaultTripStatisticsFiltering(
            "cancellationRate.lessThan=" + UPDATED_CANCELLATION_RATE,
            "cancellationRate.lessThan=" + DEFAULT_CANCELLATION_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCancellationRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where cancellationRate is greater than
        defaultTripStatisticsFiltering(
            "cancellationRate.greaterThan=" + SMALLER_CANCELLATION_RATE,
            "cancellationRate.greaterThan=" + DEFAULT_CANCELLATION_RATE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore equals to
        defaultTripStatisticsFiltering(
            "customerSatisfactionScore.equals=" + DEFAULT_CUSTOMER_SATISFACTION_SCORE,
            "customerSatisfactionScore.equals=" + UPDATED_CUSTOMER_SATISFACTION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore in
        defaultTripStatisticsFiltering(
            "customerSatisfactionScore.in=" + DEFAULT_CUSTOMER_SATISFACTION_SCORE + "," + UPDATED_CUSTOMER_SATISFACTION_SCORE,
            "customerSatisfactionScore.in=" + UPDATED_CUSTOMER_SATISFACTION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore is not null
        defaultTripStatisticsFiltering("customerSatisfactionScore.specified=true", "customerSatisfactionScore.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore is greater than or equal to
        defaultTripStatisticsFiltering(
            "customerSatisfactionScore.greaterThanOrEqual=" + DEFAULT_CUSTOMER_SATISFACTION_SCORE,
            "customerSatisfactionScore.greaterThanOrEqual=" + UPDATED_CUSTOMER_SATISFACTION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore is less than or equal to
        defaultTripStatisticsFiltering(
            "customerSatisfactionScore.lessThanOrEqual=" + DEFAULT_CUSTOMER_SATISFACTION_SCORE,
            "customerSatisfactionScore.lessThanOrEqual=" + SMALLER_CUSTOMER_SATISFACTION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore is less than
        defaultTripStatisticsFiltering(
            "customerSatisfactionScore.lessThan=" + UPDATED_CUSTOMER_SATISFACTION_SCORE,
            "customerSatisfactionScore.lessThan=" + DEFAULT_CUSTOMER_SATISFACTION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCustomerSatisfactionScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where customerSatisfactionScore is greater than
        defaultTripStatisticsFiltering(
            "customerSatisfactionScore.greaterThan=" + SMALLER_CUSTOMER_SATISFACTION_SCORE,
            "customerSatisfactionScore.greaterThan=" + DEFAULT_CUSTOMER_SATISFACTION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByMonthlyTrendIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where monthlyTrend equals to
        defaultTripStatisticsFiltering("monthlyTrend.equals=" + DEFAULT_MONTHLY_TREND, "monthlyTrend.equals=" + UPDATED_MONTHLY_TREND);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByMonthlyTrendIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where monthlyTrend in
        defaultTripStatisticsFiltering(
            "monthlyTrend.in=" + DEFAULT_MONTHLY_TREND + "," + UPDATED_MONTHLY_TREND,
            "monthlyTrend.in=" + UPDATED_MONTHLY_TREND
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByMonthlyTrendIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where monthlyTrend is not null
        defaultTripStatisticsFiltering("monthlyTrend.specified=true", "monthlyTrend.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByMonthlyTrendContainsSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where monthlyTrend contains
        defaultTripStatisticsFiltering("monthlyTrend.contains=" + DEFAULT_MONTHLY_TREND, "monthlyTrend.contains=" + UPDATED_MONTHLY_TREND);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByMonthlyTrendNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where monthlyTrend does not contain
        defaultTripStatisticsFiltering(
            "monthlyTrend.doesNotContain=" + UPDATED_MONTHLY_TREND,
            "monthlyTrend.doesNotContain=" + DEFAULT_MONTHLY_TREND
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom equals to
        defaultTripStatisticsFiltering("validFrom.equals=" + DEFAULT_VALID_FROM, "validFrom.equals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom in
        defaultTripStatisticsFiltering(
            "validFrom.in=" + DEFAULT_VALID_FROM + "," + UPDATED_VALID_FROM,
            "validFrom.in=" + UPDATED_VALID_FROM
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom is not null
        defaultTripStatisticsFiltering("validFrom.specified=true", "validFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom is greater than or equal to
        defaultTripStatisticsFiltering(
            "validFrom.greaterThanOrEqual=" + DEFAULT_VALID_FROM,
            "validFrom.greaterThanOrEqual=" + UPDATED_VALID_FROM
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom is less than or equal to
        defaultTripStatisticsFiltering(
            "validFrom.lessThanOrEqual=" + DEFAULT_VALID_FROM,
            "validFrom.lessThanOrEqual=" + SMALLER_VALID_FROM
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom is less than
        defaultTripStatisticsFiltering("validFrom.lessThan=" + UPDATED_VALID_FROM, "validFrom.lessThan=" + DEFAULT_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidFromIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validFrom is greater than
        defaultTripStatisticsFiltering("validFrom.greaterThan=" + SMALLER_VALID_FROM, "validFrom.greaterThan=" + DEFAULT_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo equals to
        defaultTripStatisticsFiltering("validTo.equals=" + DEFAULT_VALID_TO, "validTo.equals=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo in
        defaultTripStatisticsFiltering("validTo.in=" + DEFAULT_VALID_TO + "," + UPDATED_VALID_TO, "validTo.in=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo is not null
        defaultTripStatisticsFiltering("validTo.specified=true", "validTo.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo is greater than or equal to
        defaultTripStatisticsFiltering("validTo.greaterThanOrEqual=" + DEFAULT_VALID_TO, "validTo.greaterThanOrEqual=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo is less than or equal to
        defaultTripStatisticsFiltering("validTo.lessThanOrEqual=" + DEFAULT_VALID_TO, "validTo.lessThanOrEqual=" + SMALLER_VALID_TO);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo is less than
        defaultTripStatisticsFiltering("validTo.lessThan=" + UPDATED_VALID_TO, "validTo.lessThan=" + DEFAULT_VALID_TO);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByValidToIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where validTo is greater than
        defaultTripStatisticsFiltering("validTo.greaterThan=" + SMALLER_VALID_TO, "validTo.greaterThan=" + DEFAULT_VALID_TO);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where createdAt equals to
        defaultTripStatisticsFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where createdAt in
        defaultTripStatisticsFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where createdAt is not null
        defaultTripStatisticsFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where updatedAt equals to
        defaultTripStatisticsFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where updatedAt in
        defaultTripStatisticsFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where updatedAt is not null
        defaultTripStatisticsFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where isDeleted equals to
        defaultTripStatisticsFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where isDeleted in
        defaultTripStatisticsFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where isDeleted is not null
        defaultTripStatisticsFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where deletedAt equals to
        defaultTripStatisticsFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where deletedAt in
        defaultTripStatisticsFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where deletedAt is not null
        defaultTripStatisticsFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTripStatisticsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where deletedBy equals to
        defaultTripStatisticsFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllTripStatisticsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where deletedBy in
        defaultTripStatisticsFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllTripStatisticsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        // Get all the tripStatisticsList where deletedBy is not null
        defaultTripStatisticsFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultTripStatisticsFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTripStatisticsShouldBeFound(shouldBeFound);
        defaultTripStatisticsShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTripStatisticsShouldBeFound(String filter) throws Exception {
        restTripStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tripStatistics.getId().intValue())))
            .andExpect(jsonPath("$.[*].routeId").value(hasItem(DEFAULT_ROUTE_ID.intValue())))
            .andExpect(jsonPath("$.[*].vehicleType").value(hasItem(DEFAULT_VEHICLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].occasionType").value(hasItem(DEFAULT_OCCASION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].totalBookings").value(hasItem(DEFAULT_TOTAL_BOOKINGS)))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(sameNumber(DEFAULT_TOTAL_REVENUE))))
            .andExpect(jsonPath("$.[*].averagePrice").value(hasItem(sameNumber(DEFAULT_AVERAGE_PRICE))))
            .andExpect(jsonPath("$.[*].occupancyRate").value(hasItem(sameNumber(DEFAULT_OCCUPANCY_RATE))))
            .andExpect(jsonPath("$.[*].popularSeatTypes").value(hasItem(DEFAULT_POPULAR_SEAT_TYPES)))
            .andExpect(jsonPath("$.[*].peakTravelTimes").value(hasItem(DEFAULT_PEAK_TRAVEL_TIMES)))
            .andExpect(jsonPath("$.[*].cancellationRate").value(hasItem(sameNumber(DEFAULT_CANCELLATION_RATE))))
            .andExpect(jsonPath("$.[*].customerSatisfactionScore").value(hasItem(sameNumber(DEFAULT_CUSTOMER_SATISFACTION_SCORE))))
            .andExpect(jsonPath("$.[*].monthlyTrend").value(hasItem(DEFAULT_MONTHLY_TREND)))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restTripStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTripStatisticsShouldNotBeFound(String filter) throws Exception {
        restTripStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTripStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTripStatistics() throws Exception {
        // Get the tripStatistics
        restTripStatisticsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTripStatistics() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tripStatistics
        TripStatistics updatedTripStatistics = tripStatisticsRepository.findById(tripStatistics.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTripStatistics are not directly saved in db
        em.detach(updatedTripStatistics);
        updatedTripStatistics
            .routeId(UPDATED_ROUTE_ID)
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .totalBookings(UPDATED_TOTAL_BOOKINGS)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .averagePrice(UPDATED_AVERAGE_PRICE)
            .occupancyRate(UPDATED_OCCUPANCY_RATE)
            .popularSeatTypes(UPDATED_POPULAR_SEAT_TYPES)
            .peakTravelTimes(UPDATED_PEAK_TRAVEL_TIMES)
            .cancellationRate(UPDATED_CANCELLATION_RATE)
            .customerSatisfactionScore(UPDATED_CUSTOMER_SATISFACTION_SCORE)
            .monthlyTrend(UPDATED_MONTHLY_TREND)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(updatedTripStatistics);

        restTripStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripStatisticsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isOk());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTripStatisticsToMatchAllProperties(updatedTripStatistics);
    }

    @Test
    @Transactional
    void putNonExistingTripStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripStatistics.setId(longCount.incrementAndGet());

        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tripStatisticsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTripStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripStatistics.setId(longCount.incrementAndGet());

        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTripStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripStatistics.setId(longCount.incrementAndGet());

        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTripStatisticsWithPatch() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tripStatistics using partial update
        TripStatistics partialUpdatedTripStatistics = new TripStatistics();
        partialUpdatedTripStatistics.setId(tripStatistics.getId());

        partialUpdatedTripStatistics
            .routeId(UPDATED_ROUTE_ID)
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .totalBookings(UPDATED_TOTAL_BOOKINGS)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .occupancyRate(UPDATED_OCCUPANCY_RATE)
            .peakTravelTimes(UPDATED_PEAK_TRAVEL_TIMES)
            .customerSatisfactionScore(UPDATED_CUSTOMER_SATISFACTION_SCORE)
            .monthlyTrend(UPDATED_MONTHLY_TREND)
            .validFrom(UPDATED_VALID_FROM)
            .deletedBy(UPDATED_DELETED_BY);

        restTripStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTripStatistics.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTripStatistics))
            )
            .andExpect(status().isOk());

        // Validate the TripStatistics in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripStatisticsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTripStatistics, tripStatistics),
            getPersistedTripStatistics(tripStatistics)
        );
    }

    @Test
    @Transactional
    void fullUpdateTripStatisticsWithPatch() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tripStatistics using partial update
        TripStatistics partialUpdatedTripStatistics = new TripStatistics();
        partialUpdatedTripStatistics.setId(tripStatistics.getId());

        partialUpdatedTripStatistics
            .routeId(UPDATED_ROUTE_ID)
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .totalBookings(UPDATED_TOTAL_BOOKINGS)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .averagePrice(UPDATED_AVERAGE_PRICE)
            .occupancyRate(UPDATED_OCCUPANCY_RATE)
            .popularSeatTypes(UPDATED_POPULAR_SEAT_TYPES)
            .peakTravelTimes(UPDATED_PEAK_TRAVEL_TIMES)
            .cancellationRate(UPDATED_CANCELLATION_RATE)
            .customerSatisfactionScore(UPDATED_CUSTOMER_SATISFACTION_SCORE)
            .monthlyTrend(UPDATED_MONTHLY_TREND)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restTripStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTripStatistics.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTripStatistics))
            )
            .andExpect(status().isOk());

        // Validate the TripStatistics in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripStatisticsUpdatableFieldsEquals(partialUpdatedTripStatistics, getPersistedTripStatistics(partialUpdatedTripStatistics));
    }

    @Test
    @Transactional
    void patchNonExistingTripStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripStatistics.setId(longCount.incrementAndGet());

        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tripStatisticsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTripStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripStatistics.setId(longCount.incrementAndGet());

        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTripStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tripStatistics.setId(longCount.incrementAndGet());

        // Create the TripStatistics
        TripStatisticsDTO tripStatisticsDTO = tripStatisticsMapper.toDto(tripStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tripStatisticsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TripStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTripStatistics() throws Exception {
        // Initialize the database
        insertedTripStatistics = tripStatisticsRepository.saveAndFlush(tripStatistics);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tripStatistics
        restTripStatisticsMockMvc
            .perform(delete(ENTITY_API_URL_ID, tripStatistics.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tripStatisticsRepository.count();
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

    protected TripStatistics getPersistedTripStatistics(TripStatistics tripStatistics) {
        return tripStatisticsRepository.findById(tripStatistics.getId()).orElseThrow();
    }

    protected void assertPersistedTripStatisticsToMatchAllProperties(TripStatistics expectedTripStatistics) {
        assertTripStatisticsAllPropertiesEquals(expectedTripStatistics, getPersistedTripStatistics(expectedTripStatistics));
    }

    protected void assertPersistedTripStatisticsToMatchUpdatableProperties(TripStatistics expectedTripStatistics) {
        assertTripStatisticsAllUpdatablePropertiesEquals(expectedTripStatistics, getPersistedTripStatistics(expectedTripStatistics));
    }
}
