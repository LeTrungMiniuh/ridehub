package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.ScheduleAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Schedule;
import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.repository.ScheduleRepository;
import com.ridehub.route.service.dto.ScheduleDTO;
import com.ridehub.route.service.mapper.ScheduleMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleResourceIT {

    private static final String DEFAULT_SCHEDULE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SCHEDULE_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_DAYS_OF_WEEK = "AAAAAAAAAA";
    private static final String UPDATED_DAYS_OF_WEEK = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

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

    private static final String ENTITY_API_URL = "/api/schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    private Schedule insertedSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity(EntityManager em) {
        Schedule schedule = new Schedule()
            .scheduleCode(DEFAULT_SCHEDULE_CODE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .daysOfWeek(DEFAULT_DAYS_OF_WEEK)
            .active(DEFAULT_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        ScheduleOccasion scheduleOccasion;
        if (TestUtil.findAll(em, ScheduleOccasion.class).isEmpty()) {
            scheduleOccasion = ScheduleOccasionResourceIT.createEntity();
            em.persist(scheduleOccasion);
            em.flush();
        } else {
            scheduleOccasion = TestUtil.findAll(em, ScheduleOccasion.class).get(0);
        }
        schedule.setOccasionRule(scheduleOccasion);
        // Add required entity
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            route = RouteResourceIT.createEntity(em);
            em.persist(route);
            em.flush();
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        schedule.setRoute(route);
        return schedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createUpdatedEntity(EntityManager em) {
        Schedule updatedSchedule = new Schedule()
            .scheduleCode(UPDATED_SCHEDULE_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .daysOfWeek(UPDATED_DAYS_OF_WEEK)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        ScheduleOccasion scheduleOccasion;
        if (TestUtil.findAll(em, ScheduleOccasion.class).isEmpty()) {
            scheduleOccasion = ScheduleOccasionResourceIT.createUpdatedEntity();
            em.persist(scheduleOccasion);
            em.flush();
        } else {
            scheduleOccasion = TestUtil.findAll(em, ScheduleOccasion.class).get(0);
        }
        updatedSchedule.setOccasionRule(scheduleOccasion);
        // Add required entity
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            route = RouteResourceIT.createUpdatedEntity(em);
            em.persist(route);
            em.flush();
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        updatedSchedule.setRoute(route);
        return updatedSchedule;
    }

    @BeforeEach
    void initTest() {
        schedule = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSchedule != null) {
            scheduleRepository.delete(insertedSchedule);
            insertedSchedule = null;
        }
    }

    @Test
    @Transactional
    void createSchedule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);
        var returnedScheduleDTO = om.readValue(
            restScheduleMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduleDTO.class
        );

        // Validate the Schedule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSchedule = scheduleMapper.toEntity(returnedScheduleDTO);
        assertScheduleUpdatableFieldsEquals(returnedSchedule, getPersistedSchedule(returnedSchedule));

        insertedSchedule = returnedSchedule;
    }

    @Test
    @Transactional
    void createScheduleWithExistingId() throws Exception {
        // Create the Schedule with an existing ID
        schedule.setId(1L);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkScheduleCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schedule.setScheduleCode(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schedule.setActive(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schedule.setCreatedAt(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSchedules() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].scheduleCode").value(hasItem(DEFAULT_SCHEDULE_CODE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].daysOfWeek").value(hasItem(DEFAULT_DAYS_OF_WEEK)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get the schedule
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.scheduleCode").value(DEFAULT_SCHEDULE_CODE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.daysOfWeek").value(DEFAULT_DAYS_OF_WEEK))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getSchedulesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        Long id = schedule.getId();

        defaultScheduleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScheduleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScheduleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSchedulesByScheduleCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where scheduleCode equals to
        defaultScheduleFiltering("scheduleCode.equals=" + DEFAULT_SCHEDULE_CODE, "scheduleCode.equals=" + UPDATED_SCHEDULE_CODE);
    }

    @Test
    @Transactional
    void getAllSchedulesByScheduleCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where scheduleCode in
        defaultScheduleFiltering(
            "scheduleCode.in=" + DEFAULT_SCHEDULE_CODE + "," + UPDATED_SCHEDULE_CODE,
            "scheduleCode.in=" + UPDATED_SCHEDULE_CODE
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByScheduleCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where scheduleCode is not null
        defaultScheduleFiltering("scheduleCode.specified=true", "scheduleCode.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByScheduleCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where scheduleCode contains
        defaultScheduleFiltering("scheduleCode.contains=" + DEFAULT_SCHEDULE_CODE, "scheduleCode.contains=" + UPDATED_SCHEDULE_CODE);
    }

    @Test
    @Transactional
    void getAllSchedulesByScheduleCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where scheduleCode does not contain
        defaultScheduleFiltering(
            "scheduleCode.doesNotContain=" + UPDATED_SCHEDULE_CODE,
            "scheduleCode.doesNotContain=" + DEFAULT_SCHEDULE_CODE
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate equals to
        defaultScheduleFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate in
        defaultScheduleFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate is not null
        defaultScheduleFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate is greater than or equal to
        defaultScheduleFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate is less than or equal to
        defaultScheduleFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate is less than
        defaultScheduleFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where startDate is greater than
        defaultScheduleFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate equals to
        defaultScheduleFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate in
        defaultScheduleFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate is not null
        defaultScheduleFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate is greater than or equal to
        defaultScheduleFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate is less than or equal to
        defaultScheduleFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate is less than
        defaultScheduleFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where endDate is greater than
        defaultScheduleFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByDaysOfWeekIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where daysOfWeek equals to
        defaultScheduleFiltering("daysOfWeek.equals=" + DEFAULT_DAYS_OF_WEEK, "daysOfWeek.equals=" + UPDATED_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void getAllSchedulesByDaysOfWeekIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where daysOfWeek in
        defaultScheduleFiltering(
            "daysOfWeek.in=" + DEFAULT_DAYS_OF_WEEK + "," + UPDATED_DAYS_OF_WEEK,
            "daysOfWeek.in=" + UPDATED_DAYS_OF_WEEK
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByDaysOfWeekIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where daysOfWeek is not null
        defaultScheduleFiltering("daysOfWeek.specified=true", "daysOfWeek.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByDaysOfWeekContainsSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where daysOfWeek contains
        defaultScheduleFiltering("daysOfWeek.contains=" + DEFAULT_DAYS_OF_WEEK, "daysOfWeek.contains=" + UPDATED_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void getAllSchedulesByDaysOfWeekNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where daysOfWeek does not contain
        defaultScheduleFiltering("daysOfWeek.doesNotContain=" + UPDATED_DAYS_OF_WEEK, "daysOfWeek.doesNotContain=" + DEFAULT_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void getAllSchedulesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where active equals to
        defaultScheduleFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSchedulesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where active in
        defaultScheduleFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSchedulesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where active is not null
        defaultScheduleFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdAt equals to
        defaultScheduleFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdAt in
        defaultScheduleFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdAt is not null
        defaultScheduleFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where updatedAt equals to
        defaultScheduleFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSchedulesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where updatedAt in
        defaultScheduleFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSchedulesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where updatedAt is not null
        defaultScheduleFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where isDeleted equals to
        defaultScheduleFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllSchedulesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where isDeleted in
        defaultScheduleFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllSchedulesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where isDeleted is not null
        defaultScheduleFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where deletedAt equals to
        defaultScheduleFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSchedulesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where deletedAt in
        defaultScheduleFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSchedulesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where deletedAt is not null
        defaultScheduleFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where deletedBy equals to
        defaultScheduleFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where deletedBy in
        defaultScheduleFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where deletedBy is not null
        defaultScheduleFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByOccasionRuleIsEqualToSomething() throws Exception {
        ScheduleOccasion occasionRule;
        if (TestUtil.findAll(em, ScheduleOccasion.class).isEmpty()) {
            scheduleRepository.saveAndFlush(schedule);
            occasionRule = ScheduleOccasionResourceIT.createEntity();
        } else {
            occasionRule = TestUtil.findAll(em, ScheduleOccasion.class).get(0);
        }
        em.persist(occasionRule);
        em.flush();
        schedule.setOccasionRule(occasionRule);
        scheduleRepository.saveAndFlush(schedule);
        Long occasionRuleId = occasionRule.getId();
        // Get all the scheduleList where occasionRule equals to occasionRuleId
        defaultScheduleShouldBeFound("occasionRuleId.equals=" + occasionRuleId);

        // Get all the scheduleList where occasionRule equals to (occasionRuleId + 1)
        defaultScheduleShouldNotBeFound("occasionRuleId.equals=" + (occasionRuleId + 1));
    }

    @Test
    @Transactional
    void getAllSchedulesByRouteIsEqualToSomething() throws Exception {
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            scheduleRepository.saveAndFlush(schedule);
            route = RouteResourceIT.createEntity(em);
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        em.persist(route);
        em.flush();
        schedule.setRoute(route);
        scheduleRepository.saveAndFlush(schedule);
        Long routeId = route.getId();
        // Get all the scheduleList where route equals to routeId
        defaultScheduleShouldBeFound("routeId.equals=" + routeId);

        // Get all the scheduleList where route equals to (routeId + 1)
        defaultScheduleShouldNotBeFound("routeId.equals=" + (routeId + 1));
    }

    private void defaultScheduleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScheduleShouldBeFound(shouldBeFound);
        defaultScheduleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScheduleShouldBeFound(String filter) throws Exception {
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].scheduleCode").value(hasItem(DEFAULT_SCHEDULE_CODE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].daysOfWeek").value(hasItem(DEFAULT_DAYS_OF_WEEK)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScheduleShouldNotBeFound(String filter) throws Exception {
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSchedule are not directly saved in db
        em.detach(updatedSchedule);
        updatedSchedule
            .scheduleCode(UPDATED_SCHEDULE_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .daysOfWeek(UPDATED_DAYS_OF_WEEK)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(updatedSchedule);

        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduleToMatchAllProperties(updatedSchedule);
    }

    @Test
    @Transactional
    void putNonExistingSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule
            .endDate(UPDATED_END_DATE)
            .active(UPDATED_ACTIVE)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedBy(UPDATED_DELETED_BY);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSchedule, schedule), getPersistedSchedule(schedule));
    }

    @Test
    @Transactional
    void fullUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule
            .scheduleCode(UPDATED_SCHEDULE_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .daysOfWeek(UPDATED_DAYS_OF_WEEK)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleUpdatableFieldsEquals(partialUpdatedSchedule, getPersistedSchedule(partialUpdatedSchedule));
    }

    @Test
    @Transactional
    void patchNonExistingSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the schedule
        restScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, schedule.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scheduleRepository.count();
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

    protected Schedule getPersistedSchedule(Schedule schedule) {
        return scheduleRepository.findById(schedule.getId()).orElseThrow();
    }

    protected void assertPersistedScheduleToMatchAllProperties(Schedule expectedSchedule) {
        assertScheduleAllPropertiesEquals(expectedSchedule, getPersistedSchedule(expectedSchedule));
    }

    protected void assertPersistedScheduleToMatchUpdatableProperties(Schedule expectedSchedule) {
        assertScheduleAllUpdatablePropertiesEquals(expectedSchedule, getPersistedSchedule(expectedSchedule));
    }
}
