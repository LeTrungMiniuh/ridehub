package com.ridehub.user.web.rest;

import static com.ridehub.user.domain.UserStatisticsAsserts.*;
import static com.ridehub.user.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.user.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.user.IntegrationTest;
import com.ridehub.user.domain.UserStatistics;
import com.ridehub.user.repository.UserStatisticsRepository;
import com.ridehub.user.service.dto.UserStatisticsDTO;
import com.ridehub.user.service.mapper.UserStatisticsMapper;
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
 * Integration tests for the {@link UserStatisticsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserStatisticsResourceIT {

    private static final Integer DEFAULT_TOTAL_TRIPS = 1;
    private static final Integer UPDATED_TOTAL_TRIPS = 2;
    private static final Integer SMALLER_TOTAL_TRIPS = 1 - 1;

    private static final BigDecimal DEFAULT_TOTAL_SPENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_SPENT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_SPENT = new BigDecimal(1 - 1);

    private static final String DEFAULT_FAVORITE_ROUTES = "AAAAAAAAAA";
    private static final String UPDATED_FAVORITE_ROUTES = "BBBBBBBBBB";

    private static final String DEFAULT_PREFERRED_VEHICLE_TYPES = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_VEHICLE_TYPES = "BBBBBBBBBB";

    private static final Integer DEFAULT_AVERAGE_TRIP_DURATION = 1;
    private static final Integer UPDATED_AVERAGE_TRIP_DURATION = 2;
    private static final Integer SMALLER_AVERAGE_TRIP_DURATION = 1 - 1;

    private static final LocalDate DEFAULT_LAST_TRAVEL_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_TRAVEL_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_TRAVEL_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_BOOKING_FREQUENCY = "AAAAAAAAAA";
    private static final String UPDATED_BOOKING_FREQUENCY = "BBBBBBBBBB";

    private static final Integer DEFAULT_LOYALTY_POINTS = 1;
    private static final Integer UPDATED_LOYALTY_POINTS = 2;
    private static final Integer SMALLER_LOYALTY_POINTS = 1 - 1;

    private static final String DEFAULT_MOST_FREQUENT_ORIGIN = "AAAAAAAAAA";
    private static final String UPDATED_MOST_FREQUENT_ORIGIN = "BBBBBBBBBB";

    private static final String DEFAULT_MOST_FREQUENT_DESTINATION = "AAAAAAAAAA";
    private static final String UPDATED_MOST_FREQUENT_DESTINATION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AVERAGE_TRIP_DISTANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVERAGE_TRIP_DISTANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_AVERAGE_TRIP_DISTANCE = new BigDecimal(1 - 1);

    private static final String DEFAULT_PEAK_TRAVEL_TIME = "AAAAAAAAAA";
    private static final String UPDATED_PEAK_TRAVEL_TIME = "BBBBBBBBBB";

    private static final Integer DEFAULT_WEEKEND_TRIPS = 1;
    private static final Integer UPDATED_WEEKEND_TRIPS = 2;
    private static final Integer SMALLER_WEEKEND_TRIPS = 1 - 1;

    private static final Integer DEFAULT_HOLIDAY_TRIPS = 1;
    private static final Integer UPDATED_HOLIDAY_TRIPS = 2;
    private static final Integer SMALLER_HOLIDAY_TRIPS = 1 - 1;

    private static final Integer DEFAULT_CANCELLED_TRIPS = 1;
    private static final Integer UPDATED_CANCELLED_TRIPS = 2;
    private static final Integer SMALLER_CANCELLED_TRIPS = 1 - 1;

    private static final BigDecimal DEFAULT_ON_TIME_PERFORMANCE_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ON_TIME_PERFORMANCE_RATE = new BigDecimal(2);
    private static final BigDecimal SMALLER_ON_TIME_PERFORMANCE_RATE = new BigDecimal(1 - 1);

    private static final String DEFAULT_PREFERRED_SEAT_TYPES = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_SEAT_TYPES = "BBBBBBBBBB";

    private static final Integer DEFAULT_MONTHLY_TRIP_COUNT = 1;
    private static final Integer UPDATED_MONTHLY_TRIP_COUNT = 2;
    private static final Integer SMALLER_MONTHLY_TRIP_COUNT = 1 - 1;

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

    private static final String ENTITY_API_URL = "/api/user-statistics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private UserStatisticsMapper userStatisticsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserStatisticsMockMvc;

    private UserStatistics userStatistics;

    private UserStatistics insertedUserStatistics;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserStatistics createEntity(EntityManager em) {
        UserStatistics userStatistics = new UserStatistics()
            .totalTrips(DEFAULT_TOTAL_TRIPS)
            .totalSpent(DEFAULT_TOTAL_SPENT)
            .favoriteRoutes(DEFAULT_FAVORITE_ROUTES)
            .preferredVehicleTypes(DEFAULT_PREFERRED_VEHICLE_TYPES)
            .averageTripDuration(DEFAULT_AVERAGE_TRIP_DURATION)
            .lastTravelDate(DEFAULT_LAST_TRAVEL_DATE)
            .bookingFrequency(DEFAULT_BOOKING_FREQUENCY)
            .loyaltyPoints(DEFAULT_LOYALTY_POINTS)
            .mostFrequentOrigin(DEFAULT_MOST_FREQUENT_ORIGIN)
            .mostFrequentDestination(DEFAULT_MOST_FREQUENT_DESTINATION)
            .averageTripDistance(DEFAULT_AVERAGE_TRIP_DISTANCE)
            .peakTravelTime(DEFAULT_PEAK_TRAVEL_TIME)
            .weekendTrips(DEFAULT_WEEKEND_TRIPS)
            .holidayTrips(DEFAULT_HOLIDAY_TRIPS)
            .cancelledTrips(DEFAULT_CANCELLED_TRIPS)
            .onTimePerformanceRate(DEFAULT_ON_TIME_PERFORMANCE_RATE)
            .preferredSeatTypes(DEFAULT_PREFERRED_SEAT_TYPES)
            .monthlyTripCount(DEFAULT_MONTHLY_TRIP_COUNT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        return userStatistics;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserStatistics createUpdatedEntity(EntityManager em) {
        UserStatistics updatedUserStatistics = new UserStatistics()
            .totalTrips(UPDATED_TOTAL_TRIPS)
            .totalSpent(UPDATED_TOTAL_SPENT)
            .favoriteRoutes(UPDATED_FAVORITE_ROUTES)
            .preferredVehicleTypes(UPDATED_PREFERRED_VEHICLE_TYPES)
            .averageTripDuration(UPDATED_AVERAGE_TRIP_DURATION)
            .lastTravelDate(UPDATED_LAST_TRAVEL_DATE)
            .bookingFrequency(UPDATED_BOOKING_FREQUENCY)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .mostFrequentOrigin(UPDATED_MOST_FREQUENT_ORIGIN)
            .mostFrequentDestination(UPDATED_MOST_FREQUENT_DESTINATION)
            .averageTripDistance(UPDATED_AVERAGE_TRIP_DISTANCE)
            .peakTravelTime(UPDATED_PEAK_TRAVEL_TIME)
            .weekendTrips(UPDATED_WEEKEND_TRIPS)
            .holidayTrips(UPDATED_HOLIDAY_TRIPS)
            .cancelledTrips(UPDATED_CANCELLED_TRIPS)
            .onTimePerformanceRate(UPDATED_ON_TIME_PERFORMANCE_RATE)
            .preferredSeatTypes(UPDATED_PREFERRED_SEAT_TYPES)
            .monthlyTripCount(UPDATED_MONTHLY_TRIP_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        return updatedUserStatistics;
    }

    @BeforeEach
    void initTest() {
        userStatistics = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUserStatistics != null) {
            userStatisticsRepository.delete(insertedUserStatistics);
            insertedUserStatistics = null;
        }
    }

    @Test
    @Transactional
    void createUserStatistics() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);
        var returnedUserStatisticsDTO = om.readValue(
            restUserStatisticsMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(userStatisticsDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserStatisticsDTO.class
        );

        // Validate the UserStatistics in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserStatistics = userStatisticsMapper.toEntity(returnedUserStatisticsDTO);
        assertUserStatisticsUpdatableFieldsEquals(returnedUserStatistics, getPersistedUserStatistics(returnedUserStatistics));

        insertedUserStatistics = returnedUserStatistics;
    }

    @Test
    @Transactional
    void createUserStatisticsWithExistingId() throws Exception {
        // Create the UserStatistics with an existing ID
        userStatistics.setId(1L);
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userStatistics.setCreatedAt(null);

        // Create the UserStatistics, which fails.
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        restUserStatisticsMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserStatistics() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList
        restUserStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userStatistics.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalTrips").value(hasItem(DEFAULT_TOTAL_TRIPS)))
            .andExpect(jsonPath("$.[*].totalSpent").value(hasItem(sameNumber(DEFAULT_TOTAL_SPENT))))
            .andExpect(jsonPath("$.[*].favoriteRoutes").value(hasItem(DEFAULT_FAVORITE_ROUTES)))
            .andExpect(jsonPath("$.[*].preferredVehicleTypes").value(hasItem(DEFAULT_PREFERRED_VEHICLE_TYPES)))
            .andExpect(jsonPath("$.[*].averageTripDuration").value(hasItem(DEFAULT_AVERAGE_TRIP_DURATION)))
            .andExpect(jsonPath("$.[*].lastTravelDate").value(hasItem(DEFAULT_LAST_TRAVEL_DATE.toString())))
            .andExpect(jsonPath("$.[*].bookingFrequency").value(hasItem(DEFAULT_BOOKING_FREQUENCY)))
            .andExpect(jsonPath("$.[*].loyaltyPoints").value(hasItem(DEFAULT_LOYALTY_POINTS)))
            .andExpect(jsonPath("$.[*].mostFrequentOrigin").value(hasItem(DEFAULT_MOST_FREQUENT_ORIGIN)))
            .andExpect(jsonPath("$.[*].mostFrequentDestination").value(hasItem(DEFAULT_MOST_FREQUENT_DESTINATION)))
            .andExpect(jsonPath("$.[*].averageTripDistance").value(hasItem(sameNumber(DEFAULT_AVERAGE_TRIP_DISTANCE))))
            .andExpect(jsonPath("$.[*].peakTravelTime").value(hasItem(DEFAULT_PEAK_TRAVEL_TIME)))
            .andExpect(jsonPath("$.[*].weekendTrips").value(hasItem(DEFAULT_WEEKEND_TRIPS)))
            .andExpect(jsonPath("$.[*].holidayTrips").value(hasItem(DEFAULT_HOLIDAY_TRIPS)))
            .andExpect(jsonPath("$.[*].cancelledTrips").value(hasItem(DEFAULT_CANCELLED_TRIPS)))
            .andExpect(jsonPath("$.[*].onTimePerformanceRate").value(hasItem(sameNumber(DEFAULT_ON_TIME_PERFORMANCE_RATE))))
            .andExpect(jsonPath("$.[*].preferredSeatTypes").value(hasItem(DEFAULT_PREFERRED_SEAT_TYPES)))
            .andExpect(jsonPath("$.[*].monthlyTripCount").value(hasItem(DEFAULT_MONTHLY_TRIP_COUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getUserStatistics() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get the userStatistics
        restUserStatisticsMockMvc
            .perform(get(ENTITY_API_URL_ID, userStatistics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userStatistics.getId().intValue()))
            .andExpect(jsonPath("$.totalTrips").value(DEFAULT_TOTAL_TRIPS))
            .andExpect(jsonPath("$.totalSpent").value(sameNumber(DEFAULT_TOTAL_SPENT)))
            .andExpect(jsonPath("$.favoriteRoutes").value(DEFAULT_FAVORITE_ROUTES))
            .andExpect(jsonPath("$.preferredVehicleTypes").value(DEFAULT_PREFERRED_VEHICLE_TYPES))
            .andExpect(jsonPath("$.averageTripDuration").value(DEFAULT_AVERAGE_TRIP_DURATION))
            .andExpect(jsonPath("$.lastTravelDate").value(DEFAULT_LAST_TRAVEL_DATE.toString()))
            .andExpect(jsonPath("$.bookingFrequency").value(DEFAULT_BOOKING_FREQUENCY))
            .andExpect(jsonPath("$.loyaltyPoints").value(DEFAULT_LOYALTY_POINTS))
            .andExpect(jsonPath("$.mostFrequentOrigin").value(DEFAULT_MOST_FREQUENT_ORIGIN))
            .andExpect(jsonPath("$.mostFrequentDestination").value(DEFAULT_MOST_FREQUENT_DESTINATION))
            .andExpect(jsonPath("$.averageTripDistance").value(sameNumber(DEFAULT_AVERAGE_TRIP_DISTANCE)))
            .andExpect(jsonPath("$.peakTravelTime").value(DEFAULT_PEAK_TRAVEL_TIME))
            .andExpect(jsonPath("$.weekendTrips").value(DEFAULT_WEEKEND_TRIPS))
            .andExpect(jsonPath("$.holidayTrips").value(DEFAULT_HOLIDAY_TRIPS))
            .andExpect(jsonPath("$.cancelledTrips").value(DEFAULT_CANCELLED_TRIPS))
            .andExpect(jsonPath("$.onTimePerformanceRate").value(sameNumber(DEFAULT_ON_TIME_PERFORMANCE_RATE)))
            .andExpect(jsonPath("$.preferredSeatTypes").value(DEFAULT_PREFERRED_SEAT_TYPES))
            .andExpect(jsonPath("$.monthlyTripCount").value(DEFAULT_MONTHLY_TRIP_COUNT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getUserStatisticsByIdFiltering() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        Long id = userStatistics.getId();

        defaultUserStatisticsFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUserStatisticsFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUserStatisticsFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips equals to
        defaultUserStatisticsFiltering("totalTrips.equals=" + DEFAULT_TOTAL_TRIPS, "totalTrips.equals=" + UPDATED_TOTAL_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips in
        defaultUserStatisticsFiltering(
            "totalTrips.in=" + DEFAULT_TOTAL_TRIPS + "," + UPDATED_TOTAL_TRIPS,
            "totalTrips.in=" + UPDATED_TOTAL_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips is not null
        defaultUserStatisticsFiltering("totalTrips.specified=true", "totalTrips.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips is greater than or equal to
        defaultUserStatisticsFiltering(
            "totalTrips.greaterThanOrEqual=" + DEFAULT_TOTAL_TRIPS,
            "totalTrips.greaterThanOrEqual=" + UPDATED_TOTAL_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips is less than or equal to
        defaultUserStatisticsFiltering(
            "totalTrips.lessThanOrEqual=" + DEFAULT_TOTAL_TRIPS,
            "totalTrips.lessThanOrEqual=" + SMALLER_TOTAL_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips is less than
        defaultUserStatisticsFiltering("totalTrips.lessThan=" + UPDATED_TOTAL_TRIPS, "totalTrips.lessThan=" + DEFAULT_TOTAL_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalTripsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalTrips is greater than
        defaultUserStatisticsFiltering("totalTrips.greaterThan=" + SMALLER_TOTAL_TRIPS, "totalTrips.greaterThan=" + DEFAULT_TOTAL_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent equals to
        defaultUserStatisticsFiltering("totalSpent.equals=" + DEFAULT_TOTAL_SPENT, "totalSpent.equals=" + UPDATED_TOTAL_SPENT);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent in
        defaultUserStatisticsFiltering(
            "totalSpent.in=" + DEFAULT_TOTAL_SPENT + "," + UPDATED_TOTAL_SPENT,
            "totalSpent.in=" + UPDATED_TOTAL_SPENT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent is not null
        defaultUserStatisticsFiltering("totalSpent.specified=true", "totalSpent.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent is greater than or equal to
        defaultUserStatisticsFiltering(
            "totalSpent.greaterThanOrEqual=" + DEFAULT_TOTAL_SPENT,
            "totalSpent.greaterThanOrEqual=" + UPDATED_TOTAL_SPENT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent is less than or equal to
        defaultUserStatisticsFiltering(
            "totalSpent.lessThanOrEqual=" + DEFAULT_TOTAL_SPENT,
            "totalSpent.lessThanOrEqual=" + SMALLER_TOTAL_SPENT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent is less than
        defaultUserStatisticsFiltering("totalSpent.lessThan=" + UPDATED_TOTAL_SPENT, "totalSpent.lessThan=" + DEFAULT_TOTAL_SPENT);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByTotalSpentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where totalSpent is greater than
        defaultUserStatisticsFiltering("totalSpent.greaterThan=" + SMALLER_TOTAL_SPENT, "totalSpent.greaterThan=" + DEFAULT_TOTAL_SPENT);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByFavoriteRoutesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where favoriteRoutes equals to
        defaultUserStatisticsFiltering(
            "favoriteRoutes.equals=" + DEFAULT_FAVORITE_ROUTES,
            "favoriteRoutes.equals=" + UPDATED_FAVORITE_ROUTES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByFavoriteRoutesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where favoriteRoutes in
        defaultUserStatisticsFiltering(
            "favoriteRoutes.in=" + DEFAULT_FAVORITE_ROUTES + "," + UPDATED_FAVORITE_ROUTES,
            "favoriteRoutes.in=" + UPDATED_FAVORITE_ROUTES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByFavoriteRoutesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where favoriteRoutes is not null
        defaultUserStatisticsFiltering("favoriteRoutes.specified=true", "favoriteRoutes.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByFavoriteRoutesContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where favoriteRoutes contains
        defaultUserStatisticsFiltering(
            "favoriteRoutes.contains=" + DEFAULT_FAVORITE_ROUTES,
            "favoriteRoutes.contains=" + UPDATED_FAVORITE_ROUTES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByFavoriteRoutesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where favoriteRoutes does not contain
        defaultUserStatisticsFiltering(
            "favoriteRoutes.doesNotContain=" + UPDATED_FAVORITE_ROUTES,
            "favoriteRoutes.doesNotContain=" + DEFAULT_FAVORITE_ROUTES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredVehicleTypesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredVehicleTypes equals to
        defaultUserStatisticsFiltering(
            "preferredVehicleTypes.equals=" + DEFAULT_PREFERRED_VEHICLE_TYPES,
            "preferredVehicleTypes.equals=" + UPDATED_PREFERRED_VEHICLE_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredVehicleTypesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredVehicleTypes in
        defaultUserStatisticsFiltering(
            "preferredVehicleTypes.in=" + DEFAULT_PREFERRED_VEHICLE_TYPES + "," + UPDATED_PREFERRED_VEHICLE_TYPES,
            "preferredVehicleTypes.in=" + UPDATED_PREFERRED_VEHICLE_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredVehicleTypesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredVehicleTypes is not null
        defaultUserStatisticsFiltering("preferredVehicleTypes.specified=true", "preferredVehicleTypes.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredVehicleTypesContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredVehicleTypes contains
        defaultUserStatisticsFiltering(
            "preferredVehicleTypes.contains=" + DEFAULT_PREFERRED_VEHICLE_TYPES,
            "preferredVehicleTypes.contains=" + UPDATED_PREFERRED_VEHICLE_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredVehicleTypesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredVehicleTypes does not contain
        defaultUserStatisticsFiltering(
            "preferredVehicleTypes.doesNotContain=" + UPDATED_PREFERRED_VEHICLE_TYPES,
            "preferredVehicleTypes.doesNotContain=" + DEFAULT_PREFERRED_VEHICLE_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration equals to
        defaultUserStatisticsFiltering(
            "averageTripDuration.equals=" + DEFAULT_AVERAGE_TRIP_DURATION,
            "averageTripDuration.equals=" + UPDATED_AVERAGE_TRIP_DURATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration in
        defaultUserStatisticsFiltering(
            "averageTripDuration.in=" + DEFAULT_AVERAGE_TRIP_DURATION + "," + UPDATED_AVERAGE_TRIP_DURATION,
            "averageTripDuration.in=" + UPDATED_AVERAGE_TRIP_DURATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration is not null
        defaultUserStatisticsFiltering("averageTripDuration.specified=true", "averageTripDuration.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration is greater than or equal to
        defaultUserStatisticsFiltering(
            "averageTripDuration.greaterThanOrEqual=" + DEFAULT_AVERAGE_TRIP_DURATION,
            "averageTripDuration.greaterThanOrEqual=" + UPDATED_AVERAGE_TRIP_DURATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration is less than or equal to
        defaultUserStatisticsFiltering(
            "averageTripDuration.lessThanOrEqual=" + DEFAULT_AVERAGE_TRIP_DURATION,
            "averageTripDuration.lessThanOrEqual=" + SMALLER_AVERAGE_TRIP_DURATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration is less than
        defaultUserStatisticsFiltering(
            "averageTripDuration.lessThan=" + UPDATED_AVERAGE_TRIP_DURATION,
            "averageTripDuration.lessThan=" + DEFAULT_AVERAGE_TRIP_DURATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDuration is greater than
        defaultUserStatisticsFiltering(
            "averageTripDuration.greaterThan=" + SMALLER_AVERAGE_TRIP_DURATION,
            "averageTripDuration.greaterThan=" + DEFAULT_AVERAGE_TRIP_DURATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate equals to
        defaultUserStatisticsFiltering(
            "lastTravelDate.equals=" + DEFAULT_LAST_TRAVEL_DATE,
            "lastTravelDate.equals=" + UPDATED_LAST_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate in
        defaultUserStatisticsFiltering(
            "lastTravelDate.in=" + DEFAULT_LAST_TRAVEL_DATE + "," + UPDATED_LAST_TRAVEL_DATE,
            "lastTravelDate.in=" + UPDATED_LAST_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate is not null
        defaultUserStatisticsFiltering("lastTravelDate.specified=true", "lastTravelDate.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate is greater than or equal to
        defaultUserStatisticsFiltering(
            "lastTravelDate.greaterThanOrEqual=" + DEFAULT_LAST_TRAVEL_DATE,
            "lastTravelDate.greaterThanOrEqual=" + UPDATED_LAST_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate is less than or equal to
        defaultUserStatisticsFiltering(
            "lastTravelDate.lessThanOrEqual=" + DEFAULT_LAST_TRAVEL_DATE,
            "lastTravelDate.lessThanOrEqual=" + SMALLER_LAST_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate is less than
        defaultUserStatisticsFiltering(
            "lastTravelDate.lessThan=" + UPDATED_LAST_TRAVEL_DATE,
            "lastTravelDate.lessThan=" + DEFAULT_LAST_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLastTravelDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where lastTravelDate is greater than
        defaultUserStatisticsFiltering(
            "lastTravelDate.greaterThan=" + SMALLER_LAST_TRAVEL_DATE,
            "lastTravelDate.greaterThan=" + DEFAULT_LAST_TRAVEL_DATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByBookingFrequencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where bookingFrequency equals to
        defaultUserStatisticsFiltering(
            "bookingFrequency.equals=" + DEFAULT_BOOKING_FREQUENCY,
            "bookingFrequency.equals=" + UPDATED_BOOKING_FREQUENCY
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByBookingFrequencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where bookingFrequency in
        defaultUserStatisticsFiltering(
            "bookingFrequency.in=" + DEFAULT_BOOKING_FREQUENCY + "," + UPDATED_BOOKING_FREQUENCY,
            "bookingFrequency.in=" + UPDATED_BOOKING_FREQUENCY
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByBookingFrequencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where bookingFrequency is not null
        defaultUserStatisticsFiltering("bookingFrequency.specified=true", "bookingFrequency.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByBookingFrequencyContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where bookingFrequency contains
        defaultUserStatisticsFiltering(
            "bookingFrequency.contains=" + DEFAULT_BOOKING_FREQUENCY,
            "bookingFrequency.contains=" + UPDATED_BOOKING_FREQUENCY
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByBookingFrequencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where bookingFrequency does not contain
        defaultUserStatisticsFiltering(
            "bookingFrequency.doesNotContain=" + UPDATED_BOOKING_FREQUENCY,
            "bookingFrequency.doesNotContain=" + DEFAULT_BOOKING_FREQUENCY
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints equals to
        defaultUserStatisticsFiltering("loyaltyPoints.equals=" + DEFAULT_LOYALTY_POINTS, "loyaltyPoints.equals=" + UPDATED_LOYALTY_POINTS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints in
        defaultUserStatisticsFiltering(
            "loyaltyPoints.in=" + DEFAULT_LOYALTY_POINTS + "," + UPDATED_LOYALTY_POINTS,
            "loyaltyPoints.in=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints is not null
        defaultUserStatisticsFiltering("loyaltyPoints.specified=true", "loyaltyPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints is greater than or equal to
        defaultUserStatisticsFiltering(
            "loyaltyPoints.greaterThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.greaterThanOrEqual=" + UPDATED_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints is less than or equal to
        defaultUserStatisticsFiltering(
            "loyaltyPoints.lessThanOrEqual=" + DEFAULT_LOYALTY_POINTS,
            "loyaltyPoints.lessThanOrEqual=" + SMALLER_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints is less than
        defaultUserStatisticsFiltering(
            "loyaltyPoints.lessThan=" + UPDATED_LOYALTY_POINTS,
            "loyaltyPoints.lessThan=" + DEFAULT_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByLoyaltyPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where loyaltyPoints is greater than
        defaultUserStatisticsFiltering(
            "loyaltyPoints.greaterThan=" + SMALLER_LOYALTY_POINTS,
            "loyaltyPoints.greaterThan=" + DEFAULT_LOYALTY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentOriginIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentOrigin equals to
        defaultUserStatisticsFiltering(
            "mostFrequentOrigin.equals=" + DEFAULT_MOST_FREQUENT_ORIGIN,
            "mostFrequentOrigin.equals=" + UPDATED_MOST_FREQUENT_ORIGIN
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentOriginIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentOrigin in
        defaultUserStatisticsFiltering(
            "mostFrequentOrigin.in=" + DEFAULT_MOST_FREQUENT_ORIGIN + "," + UPDATED_MOST_FREQUENT_ORIGIN,
            "mostFrequentOrigin.in=" + UPDATED_MOST_FREQUENT_ORIGIN
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentOriginIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentOrigin is not null
        defaultUserStatisticsFiltering("mostFrequentOrigin.specified=true", "mostFrequentOrigin.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentOriginContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentOrigin contains
        defaultUserStatisticsFiltering(
            "mostFrequentOrigin.contains=" + DEFAULT_MOST_FREQUENT_ORIGIN,
            "mostFrequentOrigin.contains=" + UPDATED_MOST_FREQUENT_ORIGIN
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentOriginNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentOrigin does not contain
        defaultUserStatisticsFiltering(
            "mostFrequentOrigin.doesNotContain=" + UPDATED_MOST_FREQUENT_ORIGIN,
            "mostFrequentOrigin.doesNotContain=" + DEFAULT_MOST_FREQUENT_ORIGIN
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentDestinationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentDestination equals to
        defaultUserStatisticsFiltering(
            "mostFrequentDestination.equals=" + DEFAULT_MOST_FREQUENT_DESTINATION,
            "mostFrequentDestination.equals=" + UPDATED_MOST_FREQUENT_DESTINATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentDestinationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentDestination in
        defaultUserStatisticsFiltering(
            "mostFrequentDestination.in=" + DEFAULT_MOST_FREQUENT_DESTINATION + "," + UPDATED_MOST_FREQUENT_DESTINATION,
            "mostFrequentDestination.in=" + UPDATED_MOST_FREQUENT_DESTINATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentDestinationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentDestination is not null
        defaultUserStatisticsFiltering("mostFrequentDestination.specified=true", "mostFrequentDestination.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentDestinationContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentDestination contains
        defaultUserStatisticsFiltering(
            "mostFrequentDestination.contains=" + DEFAULT_MOST_FREQUENT_DESTINATION,
            "mostFrequentDestination.contains=" + UPDATED_MOST_FREQUENT_DESTINATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMostFrequentDestinationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where mostFrequentDestination does not contain
        defaultUserStatisticsFiltering(
            "mostFrequentDestination.doesNotContain=" + UPDATED_MOST_FREQUENT_DESTINATION,
            "mostFrequentDestination.doesNotContain=" + DEFAULT_MOST_FREQUENT_DESTINATION
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance equals to
        defaultUserStatisticsFiltering(
            "averageTripDistance.equals=" + DEFAULT_AVERAGE_TRIP_DISTANCE,
            "averageTripDistance.equals=" + UPDATED_AVERAGE_TRIP_DISTANCE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance in
        defaultUserStatisticsFiltering(
            "averageTripDistance.in=" + DEFAULT_AVERAGE_TRIP_DISTANCE + "," + UPDATED_AVERAGE_TRIP_DISTANCE,
            "averageTripDistance.in=" + UPDATED_AVERAGE_TRIP_DISTANCE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance is not null
        defaultUserStatisticsFiltering("averageTripDistance.specified=true", "averageTripDistance.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance is greater than or equal to
        defaultUserStatisticsFiltering(
            "averageTripDistance.greaterThanOrEqual=" + DEFAULT_AVERAGE_TRIP_DISTANCE,
            "averageTripDistance.greaterThanOrEqual=" + UPDATED_AVERAGE_TRIP_DISTANCE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance is less than or equal to
        defaultUserStatisticsFiltering(
            "averageTripDistance.lessThanOrEqual=" + DEFAULT_AVERAGE_TRIP_DISTANCE,
            "averageTripDistance.lessThanOrEqual=" + SMALLER_AVERAGE_TRIP_DISTANCE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance is less than
        defaultUserStatisticsFiltering(
            "averageTripDistance.lessThan=" + UPDATED_AVERAGE_TRIP_DISTANCE,
            "averageTripDistance.lessThan=" + DEFAULT_AVERAGE_TRIP_DISTANCE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByAverageTripDistanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where averageTripDistance is greater than
        defaultUserStatisticsFiltering(
            "averageTripDistance.greaterThan=" + SMALLER_AVERAGE_TRIP_DISTANCE,
            "averageTripDistance.greaterThan=" + DEFAULT_AVERAGE_TRIP_DISTANCE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPeakTravelTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where peakTravelTime equals to
        defaultUserStatisticsFiltering(
            "peakTravelTime.equals=" + DEFAULT_PEAK_TRAVEL_TIME,
            "peakTravelTime.equals=" + UPDATED_PEAK_TRAVEL_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPeakTravelTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where peakTravelTime in
        defaultUserStatisticsFiltering(
            "peakTravelTime.in=" + DEFAULT_PEAK_TRAVEL_TIME + "," + UPDATED_PEAK_TRAVEL_TIME,
            "peakTravelTime.in=" + UPDATED_PEAK_TRAVEL_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPeakTravelTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where peakTravelTime is not null
        defaultUserStatisticsFiltering("peakTravelTime.specified=true", "peakTravelTime.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPeakTravelTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where peakTravelTime contains
        defaultUserStatisticsFiltering(
            "peakTravelTime.contains=" + DEFAULT_PEAK_TRAVEL_TIME,
            "peakTravelTime.contains=" + UPDATED_PEAK_TRAVEL_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPeakTravelTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where peakTravelTime does not contain
        defaultUserStatisticsFiltering(
            "peakTravelTime.doesNotContain=" + UPDATED_PEAK_TRAVEL_TIME,
            "peakTravelTime.doesNotContain=" + DEFAULT_PEAK_TRAVEL_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips equals to
        defaultUserStatisticsFiltering("weekendTrips.equals=" + DEFAULT_WEEKEND_TRIPS, "weekendTrips.equals=" + UPDATED_WEEKEND_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips in
        defaultUserStatisticsFiltering(
            "weekendTrips.in=" + DEFAULT_WEEKEND_TRIPS + "," + UPDATED_WEEKEND_TRIPS,
            "weekendTrips.in=" + UPDATED_WEEKEND_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips is not null
        defaultUserStatisticsFiltering("weekendTrips.specified=true", "weekendTrips.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips is greater than or equal to
        defaultUserStatisticsFiltering(
            "weekendTrips.greaterThanOrEqual=" + DEFAULT_WEEKEND_TRIPS,
            "weekendTrips.greaterThanOrEqual=" + UPDATED_WEEKEND_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips is less than or equal to
        defaultUserStatisticsFiltering(
            "weekendTrips.lessThanOrEqual=" + DEFAULT_WEEKEND_TRIPS,
            "weekendTrips.lessThanOrEqual=" + SMALLER_WEEKEND_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips is less than
        defaultUserStatisticsFiltering("weekendTrips.lessThan=" + UPDATED_WEEKEND_TRIPS, "weekendTrips.lessThan=" + DEFAULT_WEEKEND_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByWeekendTripsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where weekendTrips is greater than
        defaultUserStatisticsFiltering(
            "weekendTrips.greaterThan=" + SMALLER_WEEKEND_TRIPS,
            "weekendTrips.greaterThan=" + DEFAULT_WEEKEND_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips equals to
        defaultUserStatisticsFiltering("holidayTrips.equals=" + DEFAULT_HOLIDAY_TRIPS, "holidayTrips.equals=" + UPDATED_HOLIDAY_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips in
        defaultUserStatisticsFiltering(
            "holidayTrips.in=" + DEFAULT_HOLIDAY_TRIPS + "," + UPDATED_HOLIDAY_TRIPS,
            "holidayTrips.in=" + UPDATED_HOLIDAY_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips is not null
        defaultUserStatisticsFiltering("holidayTrips.specified=true", "holidayTrips.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips is greater than or equal to
        defaultUserStatisticsFiltering(
            "holidayTrips.greaterThanOrEqual=" + DEFAULT_HOLIDAY_TRIPS,
            "holidayTrips.greaterThanOrEqual=" + UPDATED_HOLIDAY_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips is less than or equal to
        defaultUserStatisticsFiltering(
            "holidayTrips.lessThanOrEqual=" + DEFAULT_HOLIDAY_TRIPS,
            "holidayTrips.lessThanOrEqual=" + SMALLER_HOLIDAY_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips is less than
        defaultUserStatisticsFiltering("holidayTrips.lessThan=" + UPDATED_HOLIDAY_TRIPS, "holidayTrips.lessThan=" + DEFAULT_HOLIDAY_TRIPS);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByHolidayTripsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where holidayTrips is greater than
        defaultUserStatisticsFiltering(
            "holidayTrips.greaterThan=" + SMALLER_HOLIDAY_TRIPS,
            "holidayTrips.greaterThan=" + DEFAULT_HOLIDAY_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips equals to
        defaultUserStatisticsFiltering(
            "cancelledTrips.equals=" + DEFAULT_CANCELLED_TRIPS,
            "cancelledTrips.equals=" + UPDATED_CANCELLED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips in
        defaultUserStatisticsFiltering(
            "cancelledTrips.in=" + DEFAULT_CANCELLED_TRIPS + "," + UPDATED_CANCELLED_TRIPS,
            "cancelledTrips.in=" + UPDATED_CANCELLED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips is not null
        defaultUserStatisticsFiltering("cancelledTrips.specified=true", "cancelledTrips.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips is greater than or equal to
        defaultUserStatisticsFiltering(
            "cancelledTrips.greaterThanOrEqual=" + DEFAULT_CANCELLED_TRIPS,
            "cancelledTrips.greaterThanOrEqual=" + UPDATED_CANCELLED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips is less than or equal to
        defaultUserStatisticsFiltering(
            "cancelledTrips.lessThanOrEqual=" + DEFAULT_CANCELLED_TRIPS,
            "cancelledTrips.lessThanOrEqual=" + SMALLER_CANCELLED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips is less than
        defaultUserStatisticsFiltering(
            "cancelledTrips.lessThan=" + UPDATED_CANCELLED_TRIPS,
            "cancelledTrips.lessThan=" + DEFAULT_CANCELLED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCancelledTripsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where cancelledTrips is greater than
        defaultUserStatisticsFiltering(
            "cancelledTrips.greaterThan=" + SMALLER_CANCELLED_TRIPS,
            "cancelledTrips.greaterThan=" + DEFAULT_CANCELLED_TRIPS
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate equals to
        defaultUserStatisticsFiltering(
            "onTimePerformanceRate.equals=" + DEFAULT_ON_TIME_PERFORMANCE_RATE,
            "onTimePerformanceRate.equals=" + UPDATED_ON_TIME_PERFORMANCE_RATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate in
        defaultUserStatisticsFiltering(
            "onTimePerformanceRate.in=" + DEFAULT_ON_TIME_PERFORMANCE_RATE + "," + UPDATED_ON_TIME_PERFORMANCE_RATE,
            "onTimePerformanceRate.in=" + UPDATED_ON_TIME_PERFORMANCE_RATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate is not null
        defaultUserStatisticsFiltering("onTimePerformanceRate.specified=true", "onTimePerformanceRate.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate is greater than or equal to
        defaultUserStatisticsFiltering(
            "onTimePerformanceRate.greaterThanOrEqual=" + DEFAULT_ON_TIME_PERFORMANCE_RATE,
            "onTimePerformanceRate.greaterThanOrEqual=" + UPDATED_ON_TIME_PERFORMANCE_RATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate is less than or equal to
        defaultUserStatisticsFiltering(
            "onTimePerformanceRate.lessThanOrEqual=" + DEFAULT_ON_TIME_PERFORMANCE_RATE,
            "onTimePerformanceRate.lessThanOrEqual=" + SMALLER_ON_TIME_PERFORMANCE_RATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate is less than
        defaultUserStatisticsFiltering(
            "onTimePerformanceRate.lessThan=" + UPDATED_ON_TIME_PERFORMANCE_RATE,
            "onTimePerformanceRate.lessThan=" + DEFAULT_ON_TIME_PERFORMANCE_RATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByOnTimePerformanceRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where onTimePerformanceRate is greater than
        defaultUserStatisticsFiltering(
            "onTimePerformanceRate.greaterThan=" + SMALLER_ON_TIME_PERFORMANCE_RATE,
            "onTimePerformanceRate.greaterThan=" + DEFAULT_ON_TIME_PERFORMANCE_RATE
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredSeatTypesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredSeatTypes equals to
        defaultUserStatisticsFiltering(
            "preferredSeatTypes.equals=" + DEFAULT_PREFERRED_SEAT_TYPES,
            "preferredSeatTypes.equals=" + UPDATED_PREFERRED_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredSeatTypesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredSeatTypes in
        defaultUserStatisticsFiltering(
            "preferredSeatTypes.in=" + DEFAULT_PREFERRED_SEAT_TYPES + "," + UPDATED_PREFERRED_SEAT_TYPES,
            "preferredSeatTypes.in=" + UPDATED_PREFERRED_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredSeatTypesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredSeatTypes is not null
        defaultUserStatisticsFiltering("preferredSeatTypes.specified=true", "preferredSeatTypes.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredSeatTypesContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredSeatTypes contains
        defaultUserStatisticsFiltering(
            "preferredSeatTypes.contains=" + DEFAULT_PREFERRED_SEAT_TYPES,
            "preferredSeatTypes.contains=" + UPDATED_PREFERRED_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByPreferredSeatTypesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where preferredSeatTypes does not contain
        defaultUserStatisticsFiltering(
            "preferredSeatTypes.doesNotContain=" + UPDATED_PREFERRED_SEAT_TYPES,
            "preferredSeatTypes.doesNotContain=" + DEFAULT_PREFERRED_SEAT_TYPES
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount equals to
        defaultUserStatisticsFiltering(
            "monthlyTripCount.equals=" + DEFAULT_MONTHLY_TRIP_COUNT,
            "monthlyTripCount.equals=" + UPDATED_MONTHLY_TRIP_COUNT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount in
        defaultUserStatisticsFiltering(
            "monthlyTripCount.in=" + DEFAULT_MONTHLY_TRIP_COUNT + "," + UPDATED_MONTHLY_TRIP_COUNT,
            "monthlyTripCount.in=" + UPDATED_MONTHLY_TRIP_COUNT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount is not null
        defaultUserStatisticsFiltering("monthlyTripCount.specified=true", "monthlyTripCount.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount is greater than or equal to
        defaultUserStatisticsFiltering(
            "monthlyTripCount.greaterThanOrEqual=" + DEFAULT_MONTHLY_TRIP_COUNT,
            "monthlyTripCount.greaterThanOrEqual=" + UPDATED_MONTHLY_TRIP_COUNT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount is less than or equal to
        defaultUserStatisticsFiltering(
            "monthlyTripCount.lessThanOrEqual=" + DEFAULT_MONTHLY_TRIP_COUNT,
            "monthlyTripCount.lessThanOrEqual=" + SMALLER_MONTHLY_TRIP_COUNT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount is less than
        defaultUserStatisticsFiltering(
            "monthlyTripCount.lessThan=" + UPDATED_MONTHLY_TRIP_COUNT,
            "monthlyTripCount.lessThan=" + DEFAULT_MONTHLY_TRIP_COUNT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByMonthlyTripCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where monthlyTripCount is greater than
        defaultUserStatisticsFiltering(
            "monthlyTripCount.greaterThan=" + SMALLER_MONTHLY_TRIP_COUNT,
            "monthlyTripCount.greaterThan=" + DEFAULT_MONTHLY_TRIP_COUNT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where createdAt equals to
        defaultUserStatisticsFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where createdAt in
        defaultUserStatisticsFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where createdAt is not null
        defaultUserStatisticsFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where updatedAt equals to
        defaultUserStatisticsFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where updatedAt in
        defaultUserStatisticsFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where updatedAt is not null
        defaultUserStatisticsFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where isDeleted equals to
        defaultUserStatisticsFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where isDeleted in
        defaultUserStatisticsFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where isDeleted is not null
        defaultUserStatisticsFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where deletedAt equals to
        defaultUserStatisticsFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where deletedAt in
        defaultUserStatisticsFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where deletedAt is not null
        defaultUserStatisticsFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserStatisticsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where deletedBy equals to
        defaultUserStatisticsFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllUserStatisticsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where deletedBy in
        defaultUserStatisticsFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllUserStatisticsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        // Get all the userStatisticsList where deletedBy is not null
        defaultUserStatisticsFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultUserStatisticsFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUserStatisticsShouldBeFound(shouldBeFound);
        defaultUserStatisticsShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserStatisticsShouldBeFound(String filter) throws Exception {
        restUserStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userStatistics.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalTrips").value(hasItem(DEFAULT_TOTAL_TRIPS)))
            .andExpect(jsonPath("$.[*].totalSpent").value(hasItem(sameNumber(DEFAULT_TOTAL_SPENT))))
            .andExpect(jsonPath("$.[*].favoriteRoutes").value(hasItem(DEFAULT_FAVORITE_ROUTES)))
            .andExpect(jsonPath("$.[*].preferredVehicleTypes").value(hasItem(DEFAULT_PREFERRED_VEHICLE_TYPES)))
            .andExpect(jsonPath("$.[*].averageTripDuration").value(hasItem(DEFAULT_AVERAGE_TRIP_DURATION)))
            .andExpect(jsonPath("$.[*].lastTravelDate").value(hasItem(DEFAULT_LAST_TRAVEL_DATE.toString())))
            .andExpect(jsonPath("$.[*].bookingFrequency").value(hasItem(DEFAULT_BOOKING_FREQUENCY)))
            .andExpect(jsonPath("$.[*].loyaltyPoints").value(hasItem(DEFAULT_LOYALTY_POINTS)))
            .andExpect(jsonPath("$.[*].mostFrequentOrigin").value(hasItem(DEFAULT_MOST_FREQUENT_ORIGIN)))
            .andExpect(jsonPath("$.[*].mostFrequentDestination").value(hasItem(DEFAULT_MOST_FREQUENT_DESTINATION)))
            .andExpect(jsonPath("$.[*].averageTripDistance").value(hasItem(sameNumber(DEFAULT_AVERAGE_TRIP_DISTANCE))))
            .andExpect(jsonPath("$.[*].peakTravelTime").value(hasItem(DEFAULT_PEAK_TRAVEL_TIME)))
            .andExpect(jsonPath("$.[*].weekendTrips").value(hasItem(DEFAULT_WEEKEND_TRIPS)))
            .andExpect(jsonPath("$.[*].holidayTrips").value(hasItem(DEFAULT_HOLIDAY_TRIPS)))
            .andExpect(jsonPath("$.[*].cancelledTrips").value(hasItem(DEFAULT_CANCELLED_TRIPS)))
            .andExpect(jsonPath("$.[*].onTimePerformanceRate").value(hasItem(sameNumber(DEFAULT_ON_TIME_PERFORMANCE_RATE))))
            .andExpect(jsonPath("$.[*].preferredSeatTypes").value(hasItem(DEFAULT_PREFERRED_SEAT_TYPES)))
            .andExpect(jsonPath("$.[*].monthlyTripCount").value(hasItem(DEFAULT_MONTHLY_TRIP_COUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restUserStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserStatisticsShouldNotBeFound(String filter) throws Exception {
        restUserStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserStatistics() throws Exception {
        // Get the userStatistics
        restUserStatisticsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserStatistics() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userStatistics
        UserStatistics updatedUserStatistics = userStatisticsRepository.findById(userStatistics.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserStatistics are not directly saved in db
        em.detach(updatedUserStatistics);
        updatedUserStatistics
            .totalTrips(UPDATED_TOTAL_TRIPS)
            .totalSpent(UPDATED_TOTAL_SPENT)
            .favoriteRoutes(UPDATED_FAVORITE_ROUTES)
            .preferredVehicleTypes(UPDATED_PREFERRED_VEHICLE_TYPES)
            .averageTripDuration(UPDATED_AVERAGE_TRIP_DURATION)
            .lastTravelDate(UPDATED_LAST_TRAVEL_DATE)
            .bookingFrequency(UPDATED_BOOKING_FREQUENCY)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .mostFrequentOrigin(UPDATED_MOST_FREQUENT_ORIGIN)
            .mostFrequentDestination(UPDATED_MOST_FREQUENT_DESTINATION)
            .averageTripDistance(UPDATED_AVERAGE_TRIP_DISTANCE)
            .peakTravelTime(UPDATED_PEAK_TRAVEL_TIME)
            .weekendTrips(UPDATED_WEEKEND_TRIPS)
            .holidayTrips(UPDATED_HOLIDAY_TRIPS)
            .cancelledTrips(UPDATED_CANCELLED_TRIPS)
            .onTimePerformanceRate(UPDATED_ON_TIME_PERFORMANCE_RATE)
            .preferredSeatTypes(UPDATED_PREFERRED_SEAT_TYPES)
            .monthlyTripCount(UPDATED_MONTHLY_TRIP_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(updatedUserStatistics);

        restUserStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userStatisticsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserStatisticsToMatchAllProperties(updatedUserStatistics);
    }

    @Test
    @Transactional
    void putNonExistingUserStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userStatistics.setId(longCount.incrementAndGet());

        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userStatisticsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userStatistics.setId(longCount.incrementAndGet());

        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userStatistics.setId(longCount.incrementAndGet());

        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserStatisticsWithPatch() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userStatistics using partial update
        UserStatistics partialUpdatedUserStatistics = new UserStatistics();
        partialUpdatedUserStatistics.setId(userStatistics.getId());

        partialUpdatedUserStatistics
            .totalSpent(UPDATED_TOTAL_SPENT)
            .bookingFrequency(UPDATED_BOOKING_FREQUENCY)
            .mostFrequentDestination(UPDATED_MOST_FREQUENT_DESTINATION)
            .averageTripDistance(UPDATED_AVERAGE_TRIP_DISTANCE)
            .peakTravelTime(UPDATED_PEAK_TRAVEL_TIME)
            .weekendTrips(UPDATED_WEEKEND_TRIPS)
            .cancelledTrips(UPDATED_CANCELLED_TRIPS)
            .onTimePerformanceRate(UPDATED_ON_TIME_PERFORMANCE_RATE)
            .monthlyTripCount(UPDATED_MONTHLY_TRIP_COUNT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restUserStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserStatistics.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserStatistics))
            )
            .andExpect(status().isOk());

        // Validate the UserStatistics in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserStatisticsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserStatistics, userStatistics),
            getPersistedUserStatistics(userStatistics)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserStatisticsWithPatch() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userStatistics using partial update
        UserStatistics partialUpdatedUserStatistics = new UserStatistics();
        partialUpdatedUserStatistics.setId(userStatistics.getId());

        partialUpdatedUserStatistics
            .totalTrips(UPDATED_TOTAL_TRIPS)
            .totalSpent(UPDATED_TOTAL_SPENT)
            .favoriteRoutes(UPDATED_FAVORITE_ROUTES)
            .preferredVehicleTypes(UPDATED_PREFERRED_VEHICLE_TYPES)
            .averageTripDuration(UPDATED_AVERAGE_TRIP_DURATION)
            .lastTravelDate(UPDATED_LAST_TRAVEL_DATE)
            .bookingFrequency(UPDATED_BOOKING_FREQUENCY)
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .mostFrequentOrigin(UPDATED_MOST_FREQUENT_ORIGIN)
            .mostFrequentDestination(UPDATED_MOST_FREQUENT_DESTINATION)
            .averageTripDistance(UPDATED_AVERAGE_TRIP_DISTANCE)
            .peakTravelTime(UPDATED_PEAK_TRAVEL_TIME)
            .weekendTrips(UPDATED_WEEKEND_TRIPS)
            .holidayTrips(UPDATED_HOLIDAY_TRIPS)
            .cancelledTrips(UPDATED_CANCELLED_TRIPS)
            .onTimePerformanceRate(UPDATED_ON_TIME_PERFORMANCE_RATE)
            .preferredSeatTypes(UPDATED_PREFERRED_SEAT_TYPES)
            .monthlyTripCount(UPDATED_MONTHLY_TRIP_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restUserStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserStatistics.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserStatistics))
            )
            .andExpect(status().isOk());

        // Validate the UserStatistics in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserStatisticsUpdatableFieldsEquals(partialUpdatedUserStatistics, getPersistedUserStatistics(partialUpdatedUserStatistics));
    }

    @Test
    @Transactional
    void patchNonExistingUserStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userStatistics.setId(longCount.incrementAndGet());

        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userStatisticsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userStatistics.setId(longCount.incrementAndGet());

        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserStatistics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userStatistics.setId(longCount.incrementAndGet());

        // Create the UserStatistics
        UserStatisticsDTO userStatisticsDTO = userStatisticsMapper.toDto(userStatistics);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userStatisticsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserStatistics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserStatistics() throws Exception {
        // Initialize the database
        insertedUserStatistics = userStatisticsRepository.saveAndFlush(userStatistics);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userStatistics
        restUserStatisticsMockMvc
            .perform(delete(ENTITY_API_URL_ID, userStatistics.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userStatisticsRepository.count();
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

    protected UserStatistics getPersistedUserStatistics(UserStatistics userStatistics) {
        return userStatisticsRepository.findById(userStatistics.getId()).orElseThrow();
    }

    protected void assertPersistedUserStatisticsToMatchAllProperties(UserStatistics expectedUserStatistics) {
        assertUserStatisticsAllPropertiesEquals(expectedUserStatistics, getPersistedUserStatistics(expectedUserStatistics));
    }

    protected void assertPersistedUserStatisticsToMatchUpdatableProperties(UserStatistics expectedUserStatistics) {
        assertUserStatisticsAllUpdatablePropertiesEquals(expectedUserStatistics, getPersistedUserStatistics(expectedUserStatistics));
    }
}
