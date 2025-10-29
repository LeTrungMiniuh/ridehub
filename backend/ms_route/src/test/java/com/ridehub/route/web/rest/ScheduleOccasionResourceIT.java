package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.ScheduleOccasionAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.route.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.domain.enumeration.OccasionType;
import com.ridehub.route.repository.ScheduleOccasionRepository;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import com.ridehub.route.service.mapper.ScheduleOccasionMapper;
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
 * Integration tests for the {@link ScheduleOccasionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleOccasionResourceIT {

    private static final OccasionType DEFAULT_OCCASION = OccasionType.NORMAL;
    private static final OccasionType UPDATED_OCCASION = OccasionType.WEEKEND;

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

    private static final String ENTITY_API_URL = "/api/schedule-occasions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduleOccasionRepository scheduleOccasionRepository;

    @Autowired
    private ScheduleOccasionMapper scheduleOccasionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleOccasionMockMvc;

    private ScheduleOccasion scheduleOccasion;

    private ScheduleOccasion insertedScheduleOccasion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleOccasion createEntity() {
        return new ScheduleOccasion()
            .occasion(DEFAULT_OCCASION)
            .occasionFactor(DEFAULT_OCCASION_FACTOR)
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
    public static ScheduleOccasion createUpdatedEntity() {
        return new ScheduleOccasion()
            .occasion(UPDATED_OCCASION)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        scheduleOccasion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedScheduleOccasion != null) {
            scheduleOccasionRepository.delete(insertedScheduleOccasion);
            insertedScheduleOccasion = null;
        }
    }

    @Test
    @Transactional
    void createScheduleOccasion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);
        var returnedScheduleOccasionDTO = om.readValue(
            restScheduleOccasionMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(scheduleOccasionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduleOccasionDTO.class
        );

        // Validate the ScheduleOccasion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScheduleOccasion = scheduleOccasionMapper.toEntity(returnedScheduleOccasionDTO);
        assertScheduleOccasionUpdatableFieldsEquals(returnedScheduleOccasion, getPersistedScheduleOccasion(returnedScheduleOccasion));

        insertedScheduleOccasion = returnedScheduleOccasion;
    }

    @Test
    @Transactional
    void createScheduleOccasionWithExistingId() throws Exception {
        // Create the ScheduleOccasion with an existing ID
        scheduleOccasion.setId(1L);
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleOccasionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOccasionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleOccasion.setOccasion(null);

        // Create the ScheduleOccasion, which fails.
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        restScheduleOccasionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOccasionFactorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleOccasion.setOccasionFactor(null);

        // Create the ScheduleOccasion, which fails.
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        restScheduleOccasionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduleOccasion.setCreatedAt(null);

        // Create the ScheduleOccasion, which fails.
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        restScheduleOccasionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScheduleOccasions() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList
        restScheduleOccasionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleOccasion.getId().intValue())))
            .andExpect(jsonPath("$.[*].occasion").value(hasItem(DEFAULT_OCCASION.toString())))
            .andExpect(jsonPath("$.[*].occasionFactor").value(hasItem(sameNumber(DEFAULT_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getScheduleOccasion() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get the scheduleOccasion
        restScheduleOccasionMockMvc
            .perform(get(ENTITY_API_URL_ID, scheduleOccasion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scheduleOccasion.getId().intValue()))
            .andExpect(jsonPath("$.occasion").value(DEFAULT_OCCASION.toString()))
            .andExpect(jsonPath("$.occasionFactor").value(sameNumber(DEFAULT_OCCASION_FACTOR)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getScheduleOccasionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        Long id = scheduleOccasion.getId();

        defaultScheduleOccasionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScheduleOccasionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScheduleOccasionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasion equals to
        defaultScheduleOccasionFiltering("occasion.equals=" + DEFAULT_OCCASION, "occasion.equals=" + UPDATED_OCCASION);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasion in
        defaultScheduleOccasionFiltering("occasion.in=" + DEFAULT_OCCASION + "," + UPDATED_OCCASION, "occasion.in=" + UPDATED_OCCASION);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasion is not null
        defaultScheduleOccasionFiltering("occasion.specified=true", "occasion.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor equals to
        defaultScheduleOccasionFiltering(
            "occasionFactor.equals=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.equals=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor in
        defaultScheduleOccasionFiltering(
            "occasionFactor.in=" + DEFAULT_OCCASION_FACTOR + "," + UPDATED_OCCASION_FACTOR,
            "occasionFactor.in=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor is not null
        defaultScheduleOccasionFiltering("occasionFactor.specified=true", "occasionFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor is greater than or equal to
        defaultScheduleOccasionFiltering(
            "occasionFactor.greaterThanOrEqual=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.greaterThanOrEqual=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor is less than or equal to
        defaultScheduleOccasionFiltering(
            "occasionFactor.lessThanOrEqual=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.lessThanOrEqual=" + SMALLER_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor is less than
        defaultScheduleOccasionFiltering(
            "occasionFactor.lessThan=" + UPDATED_OCCASION_FACTOR,
            "occasionFactor.lessThan=" + DEFAULT_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByOccasionFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where occasionFactor is greater than
        defaultScheduleOccasionFiltering(
            "occasionFactor.greaterThan=" + SMALLER_OCCASION_FACTOR,
            "occasionFactor.greaterThan=" + DEFAULT_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where createdAt equals to
        defaultScheduleOccasionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where createdAt in
        defaultScheduleOccasionFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where createdAt is not null
        defaultScheduleOccasionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where updatedAt equals to
        defaultScheduleOccasionFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where updatedAt in
        defaultScheduleOccasionFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where updatedAt is not null
        defaultScheduleOccasionFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where isDeleted equals to
        defaultScheduleOccasionFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where isDeleted in
        defaultScheduleOccasionFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where isDeleted is not null
        defaultScheduleOccasionFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where deletedAt equals to
        defaultScheduleOccasionFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where deletedAt in
        defaultScheduleOccasionFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where deletedAt is not null
        defaultScheduleOccasionFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where deletedBy equals to
        defaultScheduleOccasionFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where deletedBy in
        defaultScheduleOccasionFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllScheduleOccasionsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        // Get all the scheduleOccasionList where deletedBy is not null
        defaultScheduleOccasionFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultScheduleOccasionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScheduleOccasionShouldBeFound(shouldBeFound);
        defaultScheduleOccasionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScheduleOccasionShouldBeFound(String filter) throws Exception {
        restScheduleOccasionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleOccasion.getId().intValue())))
            .andExpect(jsonPath("$.[*].occasion").value(hasItem(DEFAULT_OCCASION.toString())))
            .andExpect(jsonPath("$.[*].occasionFactor").value(hasItem(sameNumber(DEFAULT_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restScheduleOccasionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScheduleOccasionShouldNotBeFound(String filter) throws Exception {
        restScheduleOccasionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScheduleOccasionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScheduleOccasion() throws Exception {
        // Get the scheduleOccasion
        restScheduleOccasionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScheduleOccasion() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleOccasion
        ScheduleOccasion updatedScheduleOccasion = scheduleOccasionRepository.findById(scheduleOccasion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScheduleOccasion are not directly saved in db
        em.detach(updatedScheduleOccasion);
        updatedScheduleOccasion
            .occasion(UPDATED_OCCASION)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(updatedScheduleOccasion);

        restScheduleOccasionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleOccasionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduleOccasionToMatchAllProperties(updatedScheduleOccasion);
    }

    @Test
    @Transactional
    void putNonExistingScheduleOccasion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleOccasion.setId(longCount.incrementAndGet());

        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleOccasionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleOccasionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScheduleOccasion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleOccasion.setId(longCount.incrementAndGet());

        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleOccasionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScheduleOccasion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleOccasion.setId(longCount.incrementAndGet());

        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleOccasionMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleOccasionWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleOccasion using partial update
        ScheduleOccasion partialUpdatedScheduleOccasion = new ScheduleOccasion();
        partialUpdatedScheduleOccasion.setId(scheduleOccasion.getId());

        partialUpdatedScheduleOccasion.occasion(UPDATED_OCCASION).isDeleted(UPDATED_IS_DELETED).deletedBy(UPDATED_DELETED_BY);

        restScheduleOccasionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleOccasion.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleOccasion))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleOccasion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleOccasionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScheduleOccasion, scheduleOccasion),
            getPersistedScheduleOccasion(scheduleOccasion)
        );
    }

    @Test
    @Transactional
    void fullUpdateScheduleOccasionWithPatch() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduleOccasion using partial update
        ScheduleOccasion partialUpdatedScheduleOccasion = new ScheduleOccasion();
        partialUpdatedScheduleOccasion.setId(scheduleOccasion.getId());

        partialUpdatedScheduleOccasion
            .occasion(UPDATED_OCCASION)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restScheduleOccasionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduleOccasion.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduleOccasion))
            )
            .andExpect(status().isOk());

        // Validate the ScheduleOccasion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleOccasionUpdatableFieldsEquals(
            partialUpdatedScheduleOccasion,
            getPersistedScheduleOccasion(partialUpdatedScheduleOccasion)
        );
    }

    @Test
    @Transactional
    void patchNonExistingScheduleOccasion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleOccasion.setId(longCount.incrementAndGet());

        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleOccasionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleOccasionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScheduleOccasion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleOccasion.setId(longCount.incrementAndGet());

        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleOccasionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScheduleOccasion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleOccasion.setId(longCount.incrementAndGet());

        // Create the ScheduleOccasion
        ScheduleOccasionDTO scheduleOccasionDTO = scheduleOccasionMapper.toDto(scheduleOccasion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleOccasionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleOccasionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduleOccasion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScheduleOccasion() throws Exception {
        // Initialize the database
        insertedScheduleOccasion = scheduleOccasionRepository.saveAndFlush(scheduleOccasion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scheduleOccasion
        restScheduleOccasionMockMvc
            .perform(delete(ENTITY_API_URL_ID, scheduleOccasion.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scheduleOccasionRepository.count();
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

    protected ScheduleOccasion getPersistedScheduleOccasion(ScheduleOccasion scheduleOccasion) {
        return scheduleOccasionRepository.findById(scheduleOccasion.getId()).orElseThrow();
    }

    protected void assertPersistedScheduleOccasionToMatchAllProperties(ScheduleOccasion expectedScheduleOccasion) {
        assertScheduleOccasionAllPropertiesEquals(expectedScheduleOccasion, getPersistedScheduleOccasion(expectedScheduleOccasion));
    }

    protected void assertPersistedScheduleOccasionToMatchUpdatableProperties(ScheduleOccasion expectedScheduleOccasion) {
        assertScheduleOccasionAllUpdatablePropertiesEquals(
            expectedScheduleOccasion,
            getPersistedScheduleOccasion(expectedScheduleOccasion)
        );
    }
}
