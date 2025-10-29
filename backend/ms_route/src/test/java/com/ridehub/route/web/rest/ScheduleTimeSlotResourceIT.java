package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.ScheduleTimeSlotAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Schedule;
import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.repository.ScheduleTimeSlotRepository;
import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
import com.ridehub.route.service.mapper.ScheduleTimeSlotMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalTime;
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
 * Integration tests for the {@link ScheduleTimeSlotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleTimeSlotResourceIT {

    private static final String DEFAULT_SLOT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SLOT_CODE = "BBBBBBBBBB";

    private static final LocalTime DEFAULT_DEPARTURE_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_DEPARTURE_TIME = LocalTime.MAX.withNano(0);

    private static final LocalTime DEFAULT_ARRIVAL_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_ARRIVAL_TIME = LocalTime.MAX.withNano(0);

    private static final Integer DEFAULT_BUFFER_MINUTES = 1;
    private static final Integer UPDATED_BUFFER_MINUTES = 2;
    private static final Integer SMALLER_BUFFER_MINUTES = 1 - 1;

    private static final Integer DEFAULT_SEQUENCE = 1;
    private static final Integer UPDATED_SEQUENCE = 2;
    private static final Integer SMALLER_SEQUENCE = 1 - 1;

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

    private static final String ENTITY_API_URL = "/api/schedule-time-slots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduleTimeSlotRepository scheduleTimeSlotRepository;

    @Autowired
    private ScheduleTimeSlotMapper scheduleTimeSlotMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleTimeSlotMockMvc;

    private ScheduleTimeSlot scheduleTimeSlot;

    private ScheduleTimeSlot insertedScheduleTimeSlot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleTimeSlot createEntity(EntityManager em) {
        ScheduleTimeSlot scheduleTimeSlot = new ScheduleTimeSlot()
            .slotCode(DEFAULT_SLOT_CODE)
            .departureTime(DEFAULT_DEPARTURE_TIME)
            .arrivalTime(DEFAULT_ARRIVAL_TIME)
            .bufferMinutes(DEFAULT_BUFFER_MINUTES)
            .sequence(DEFAULT_SEQUENCE)
            .active(DEFAULT_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Schedule schedule;
        if (TestUtil.findAll(em, Schedule.class).isEmpty()) {
            schedule = ScheduleResourceIT.createEntity(em);
            em.persist(schedule);
            em.flush();
        } else {
            schedule = TestUtil.findAll(em, Schedule.class).get(0);
        }
        scheduleTimeSlot.setSchedule(schedule);
        return scheduleTimeSlot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleTimeSlot createUpdatedEntity(EntityManager em) {
        ScheduleTimeSlot updatedScheduleTimeSlot = new ScheduleTimeSlot()
            .slotCode(UPDATED_SLOT_CODE)
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .bufferMinutes(UPDATED_BUFFER_MINUTES)
            .sequence(UPDATED_SEQUENCE)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Schedule schedule;
        if (TestUtil.findAll(em, Schedule.class).isEmpty()) {
            schedule = ScheduleResourceIT.createUpdatedEntity(em);
            em.persist(schedule);
            em.flush();
        } else {
            schedule = TestUtil.findAll(em, Schedule.class).get(0);
        }
        updatedScheduleTimeSlot.setSchedule(schedule);
        return updatedScheduleTimeSlot;
    }

    @BeforeEach
    void initTest() {
        scheduleTimeSlot = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedScheduleTimeSlot != null) {
            scheduleTimeSlotRepository.delete(insertedScheduleTimeSlot);
            insertedScheduleTimeSlot = null;
        }
    }

    @Test
    @Transactional
    void createScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);
        var returnedScheduleTimeSlotDTO = om.readValue(
            restScheduleTimeSlotMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduleTimeSlotDTO.class
        );

        // Validate the ScheduleTimeSlot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScheduleTimeSlot = scheduleTimeSlotMapper.toEntity(returnedScheduleTimeSlotDTO);
        assertScheduleTimeSlotUpdatableFieldsEquals(returnedScheduleTimeSlot, getPersistedScheduleTimeSlot(returnedScheduleTimeSlot));

        insertedScheduleTimeSlot = returnedScheduleTimeSlot;
    }

    @Test
    @Transactional
    void createScheduleTimeSlotWithExistingId() throws Exception {
        // Create the ScheduleTimeSlot with an existing ID
        scheduleTimeSlot.setId(1L);
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleTimeSlotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSlotCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleTimeSlot.setSlotCode(null);

        // Create the ScheduleTimeSlot, which fails.
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        restScheduleTimeSlotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDepartureTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleTimeSlot.setDepartureTime(null);

        // Create the ScheduleTimeSlot, which fails.
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        restScheduleTimeSlotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArrivalTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleTimeSlot.setArrivalTime(null);

        // Create the ScheduleTimeSlot, which fails.
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        restScheduleTimeSlotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleTimeSlot.setActive(null);

        // Create the ScheduleTimeSlot, which fails.
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        restScheduleTimeSlotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleTimeSlot.setCreatedAt(null);

        // Create the ScheduleTimeSlot, which fails.
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        restScheduleTimeSlotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlots() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList
        restScheduleTimeSlotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleTimeSlot.getId().intValue())))
            .andExpect(jsonPath("$.[*].slotCode").value(hasItem(DEFAULT_SLOT_CODE)))
            .andExpect(jsonPath("$.[*].departureTime").value(hasItem(DEFAULT_DEPARTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].arrivalTime").value(hasItem(DEFAULT_ARRIVAL_TIME.toString())))
            .andExpect(jsonPath("$.[*].bufferMinutes").value(hasItem(DEFAULT_BUFFER_MINUTES)))
            .andExpect(jsonPath("$.[*].sequence").value(hasItem(DEFAULT_SEQUENCE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getScheduleTimeSlot() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get the scheduleTimeSlot
        restScheduleTimeSlotMockMvc
            .perform(get(ENTITY_API_URL_ID, scheduleTimeSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scheduleTimeSlot.getId().intValue()))
            .andExpect(jsonPath("$.slotCode").value(DEFAULT_SLOT_CODE))
            .andExpect(jsonPath("$.departureTime").value(DEFAULT_DEPARTURE_TIME.toString()))
            .andExpect(jsonPath("$.arrivalTime").value(DEFAULT_ARRIVAL_TIME.toString()))
            .andExpect(jsonPath("$.bufferMinutes").value(DEFAULT_BUFFER_MINUTES))
            .andExpect(jsonPath("$.sequence").value(DEFAULT_SEQUENCE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getScheduleTimeSlotsByIdFiltering() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        Long id = scheduleTimeSlot.getId();

        defaultScheduleTimeSlotFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScheduleTimeSlotFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScheduleTimeSlotFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySlotCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where slotCode equals to
        defaultScheduleTimeSlotFiltering("slotCode.equals=" + DEFAULT_SLOT_CODE, "slotCode.equals=" + UPDATED_SLOT_CODE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySlotCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where slotCode in
        defaultScheduleTimeSlotFiltering("slotCode.in=" + DEFAULT_SLOT_CODE + "," + UPDATED_SLOT_CODE, "slotCode.in=" + UPDATED_SLOT_CODE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySlotCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where slotCode is not null
        defaultScheduleTimeSlotFiltering("slotCode.specified=true", "slotCode.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySlotCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where slotCode contains
        defaultScheduleTimeSlotFiltering("slotCode.contains=" + DEFAULT_SLOT_CODE, "slotCode.contains=" + UPDATED_SLOT_CODE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySlotCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where slotCode does not contain
        defaultScheduleTimeSlotFiltering("slotCode.doesNotContain=" + UPDATED_SLOT_CODE, "slotCode.doesNotContain=" + DEFAULT_SLOT_CODE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDepartureTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where departureTime equals to
        defaultScheduleTimeSlotFiltering(
            "departureTime.equals=" + DEFAULT_DEPARTURE_TIME,
            "departureTime.equals=" + UPDATED_DEPARTURE_TIME
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDepartureTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where departureTime in
        defaultScheduleTimeSlotFiltering(
            "departureTime.in=" + DEFAULT_DEPARTURE_TIME + "," + UPDATED_DEPARTURE_TIME,
            "departureTime.in=" + UPDATED_DEPARTURE_TIME
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDepartureTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where departureTime is not null
        defaultScheduleTimeSlotFiltering("departureTime.specified=true", "departureTime.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByArrivalTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where arrivalTime equals to
        defaultScheduleTimeSlotFiltering("arrivalTime.equals=" + DEFAULT_ARRIVAL_TIME, "arrivalTime.equals=" + UPDATED_ARRIVAL_TIME);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByArrivalTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where arrivalTime in
        defaultScheduleTimeSlotFiltering(
            "arrivalTime.in=" + DEFAULT_ARRIVAL_TIME + "," + UPDATED_ARRIVAL_TIME,
            "arrivalTime.in=" + UPDATED_ARRIVAL_TIME
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByArrivalTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where arrivalTime is not null
        defaultScheduleTimeSlotFiltering("arrivalTime.specified=true", "arrivalTime.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes equals to
        defaultScheduleTimeSlotFiltering(
            "bufferMinutes.equals=" + DEFAULT_BUFFER_MINUTES,
            "bufferMinutes.equals=" + UPDATED_BUFFER_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes in
        defaultScheduleTimeSlotFiltering(
            "bufferMinutes.in=" + DEFAULT_BUFFER_MINUTES + "," + UPDATED_BUFFER_MINUTES,
            "bufferMinutes.in=" + UPDATED_BUFFER_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes is not null
        defaultScheduleTimeSlotFiltering("bufferMinutes.specified=true", "bufferMinutes.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes is greater than or equal to
        defaultScheduleTimeSlotFiltering(
            "bufferMinutes.greaterThanOrEqual=" + DEFAULT_BUFFER_MINUTES,
            "bufferMinutes.greaterThanOrEqual=" + UPDATED_BUFFER_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes is less than or equal to
        defaultScheduleTimeSlotFiltering(
            "bufferMinutes.lessThanOrEqual=" + DEFAULT_BUFFER_MINUTES,
            "bufferMinutes.lessThanOrEqual=" + SMALLER_BUFFER_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes is less than
        defaultScheduleTimeSlotFiltering(
            "bufferMinutes.lessThan=" + UPDATED_BUFFER_MINUTES,
            "bufferMinutes.lessThan=" + DEFAULT_BUFFER_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByBufferMinutesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where bufferMinutes is greater than
        defaultScheduleTimeSlotFiltering(
            "bufferMinutes.greaterThan=" + SMALLER_BUFFER_MINUTES,
            "bufferMinutes.greaterThan=" + DEFAULT_BUFFER_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence equals to
        defaultScheduleTimeSlotFiltering("sequence.equals=" + DEFAULT_SEQUENCE, "sequence.equals=" + UPDATED_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence in
        defaultScheduleTimeSlotFiltering("sequence.in=" + DEFAULT_SEQUENCE + "," + UPDATED_SEQUENCE, "sequence.in=" + UPDATED_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence is not null
        defaultScheduleTimeSlotFiltering("sequence.specified=true", "sequence.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence is greater than or equal to
        defaultScheduleTimeSlotFiltering(
            "sequence.greaterThanOrEqual=" + DEFAULT_SEQUENCE,
            "sequence.greaterThanOrEqual=" + UPDATED_SEQUENCE
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence is less than or equal to
        defaultScheduleTimeSlotFiltering("sequence.lessThanOrEqual=" + DEFAULT_SEQUENCE, "sequence.lessThanOrEqual=" + SMALLER_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence is less than
        defaultScheduleTimeSlotFiltering("sequence.lessThan=" + UPDATED_SEQUENCE, "sequence.lessThan=" + DEFAULT_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsBySequenceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where sequence is greater than
        defaultScheduleTimeSlotFiltering("sequence.greaterThan=" + SMALLER_SEQUENCE, "sequence.greaterThan=" + DEFAULT_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where active equals to
        defaultScheduleTimeSlotFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where active in
        defaultScheduleTimeSlotFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where active is not null
        defaultScheduleTimeSlotFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where createdAt equals to
        defaultScheduleTimeSlotFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where createdAt in
        defaultScheduleTimeSlotFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where createdAt is not null
        defaultScheduleTimeSlotFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where updatedAt equals to
        defaultScheduleTimeSlotFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where updatedAt in
        defaultScheduleTimeSlotFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where updatedAt is not null
        defaultScheduleTimeSlotFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where isDeleted equals to
        defaultScheduleTimeSlotFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where isDeleted in
        defaultScheduleTimeSlotFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where isDeleted is not null
        defaultScheduleTimeSlotFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where deletedAt equals to
        defaultScheduleTimeSlotFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where deletedAt in
        defaultScheduleTimeSlotFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where deletedAt is not null
        defaultScheduleTimeSlotFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where deletedBy equals to
        defaultScheduleTimeSlotFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where deletedBy in
        defaultScheduleTimeSlotFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        // Get all the scheduleTimeSlotList where deletedBy is not null
        defaultScheduleTimeSlotFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleTimeSlotsByScheduleIsEqualToSomething() throws Exception {
        Schedule schedule;
        if (TestUtil.findAll(em, Schedule.class).isEmpty()) {
            scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);
            schedule = ScheduleResourceIT.createEntity(em);
        } else {
            schedule = TestUtil.findAll(em, Schedule.class).get(0);
        }
        em.persist(schedule);
        em.flush();
        scheduleTimeSlot.setSchedule(schedule);
        scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);
        Long scheduleId = schedule.getId();
        // Get all the scheduleTimeSlotList where schedule equals to scheduleId
        defaultScheduleTimeSlotShouldBeFound("scheduleId.equals=" + scheduleId);

        // Get all the scheduleTimeSlotList where schedule equals to (scheduleId + 1)
        defaultScheduleTimeSlotShouldNotBeFound("scheduleId.equals=" + (scheduleId + 1));
    }

    private void defaultScheduleTimeSlotFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScheduleTimeSlotShouldBeFound(shouldBeFound);
        defaultScheduleTimeSlotShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScheduleTimeSlotShouldBeFound(String filter) throws Exception {
        restScheduleTimeSlotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleTimeSlot.getId().intValue())))
            .andExpect(jsonPath("$.[*].slotCode").value(hasItem(DEFAULT_SLOT_CODE)))
            .andExpect(jsonPath("$.[*].departureTime").value(hasItem(DEFAULT_DEPARTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].arrivalTime").value(hasItem(DEFAULT_ARRIVAL_TIME.toString())))
            .andExpect(jsonPath("$.[*].bufferMinutes").value(hasItem(DEFAULT_BUFFER_MINUTES)))
            .andExpect(jsonPath("$.[*].sequence").value(hasItem(DEFAULT_SEQUENCE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restScheduleTimeSlotMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScheduleTimeSlotShouldNotBeFound(String filter) throws Exception {
        restScheduleTimeSlotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScheduleTimeSlotMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScheduleTimeSlot() throws Exception {
        // Get the scheduleTimeSlot
        restScheduleTimeSlotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScheduleTimeSlot() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleTimeSlot
        ScheduleTimeSlot updatedScheduleTimeSlot = scheduleTimeSlotRepository.findById(scheduleTimeSlot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScheduleTimeSlot are not directly saved in db
        em.detach(updatedScheduleTimeSlot);
        updatedScheduleTimeSlot
            .slotCode(UPDATED_SLOT_CODE)
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .bufferMinutes(UPDATED_BUFFER_MINUTES)
            .sequence(UPDATED_SEQUENCE)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(updatedScheduleTimeSlot);

        restScheduleTimeSlotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleTimeSlotDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduleTimeSlotToMatchAllProperties(updatedScheduleTimeSlot);
    }

    @Test
    @Transactional
    void putNonExistingScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleTimeSlot.setId(longCount.incrementAndGet());

        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleTimeSlotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleTimeSlotDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleTimeSlot.setId(longCount.incrementAndGet());

        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleTimeSlotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleTimeSlot.setId(longCount.incrementAndGet());

        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleTimeSlotMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleTimeSlotWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleTimeSlot using partial update
        ScheduleTimeSlot partialUpdatedScheduleTimeSlot = new ScheduleTimeSlot();
        partialUpdatedScheduleTimeSlot.setId(scheduleTimeSlot.getId());

        partialUpdatedScheduleTimeSlot.slotCode(UPDATED_SLOT_CODE).bufferMinutes(UPDATED_BUFFER_MINUTES);

        restScheduleTimeSlotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleTimeSlot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleTimeSlot))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleTimeSlot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleTimeSlotUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScheduleTimeSlot, scheduleTimeSlot),
            getPersistedScheduleTimeSlot(scheduleTimeSlot)
        );
    }

    @Test
    @Transactional
    void fullUpdateScheduleTimeSlotWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleTimeSlot using partial update
        ScheduleTimeSlot partialUpdatedScheduleTimeSlot = new ScheduleTimeSlot();
        partialUpdatedScheduleTimeSlot.setId(scheduleTimeSlot.getId());

        partialUpdatedScheduleTimeSlot
            .slotCode(UPDATED_SLOT_CODE)
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .bufferMinutes(UPDATED_BUFFER_MINUTES)
            .sequence(UPDATED_SEQUENCE)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restScheduleTimeSlotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleTimeSlot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleTimeSlot))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleTimeSlot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleTimeSlotUpdatableFieldsEquals(
            partialUpdatedScheduleTimeSlot,
            getPersistedScheduleTimeSlot(partialUpdatedScheduleTimeSlot)
        );
    }

    @Test
    @Transactional
    void patchNonExistingScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleTimeSlot.setId(longCount.incrementAndGet());

        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleTimeSlotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleTimeSlotDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleTimeSlot.setId(longCount.incrementAndGet());

        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleTimeSlotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScheduleTimeSlot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleTimeSlot.setId(longCount.incrementAndGet());

        // Create the ScheduleTimeSlot
        ScheduleTimeSlotDTO scheduleTimeSlotDTO = scheduleTimeSlotMapper.toDto(scheduleTimeSlot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleTimeSlotMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleTimeSlotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleTimeSlot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScheduleTimeSlot() throws Exception {
        // Initialize the database
        insertedScheduleTimeSlot = scheduleTimeSlotRepository.saveAndFlush(scheduleTimeSlot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scheduleTimeSlot
        restScheduleTimeSlotMockMvc
            .perform(delete(ENTITY_API_URL_ID, scheduleTimeSlot.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scheduleTimeSlotRepository.count();
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

    protected ScheduleTimeSlot getPersistedScheduleTimeSlot(ScheduleTimeSlot scheduleTimeSlot) {
        return scheduleTimeSlotRepository.findById(scheduleTimeSlot.getId()).orElseThrow();
    }

    protected void assertPersistedScheduleTimeSlotToMatchAllProperties(ScheduleTimeSlot expectedScheduleTimeSlot) {
        assertScheduleTimeSlotAllPropertiesEquals(expectedScheduleTimeSlot, getPersistedScheduleTimeSlot(expectedScheduleTimeSlot));
    }

    protected void assertPersistedScheduleTimeSlotToMatchUpdatableProperties(ScheduleTimeSlot expectedScheduleTimeSlot) {
        assertScheduleTimeSlotAllUpdatablePropertiesEquals(
            expectedScheduleTimeSlot,
            getPersistedScheduleTimeSlot(expectedScheduleTimeSlot)
        );
    }
}
