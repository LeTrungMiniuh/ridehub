package com.ridehub.user.web.rest;

import static com.ridehub.user.domain.UserQueryAsserts.*;
import static com.ridehub.user.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.user.IntegrationTest;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.domain.UserQuery;
import com.ridehub.user.repository.UserQueryRepository;
import com.ridehub.user.service.dto.UserQueryDTO;
import com.ridehub.user.service.mapper.UserQueryMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link UserQueryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserQueryResourceIT {

    private static final String DEFAULT_QUERY_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_QUERY_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_QUERY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_QUERY_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_PARAMETERS = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETERS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESPONSE_GENERATED = false;
    private static final Boolean UPDATED_RESPONSE_GENERATED = true;

    private static final Integer DEFAULT_RESPONSE_TIME = 1;
    private static final Integer UPDATED_RESPONSE_TIME = 2;
    private static final Integer SMALLER_RESPONSE_TIME = 1 - 1;

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

    private static final String ENTITY_API_URL = "/api/user-queries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private UserQueryMapper userQueryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserQueryMockMvc;

    private UserQuery userQuery;

    private UserQuery insertedUserQuery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserQuery createEntity(EntityManager em) {
        UserQuery userQuery = new UserQuery()
            .queryText(DEFAULT_QUERY_TEXT)
            .queryType(DEFAULT_QUERY_TYPE)
            .parameters(DEFAULT_PARAMETERS)
            .responseGenerated(DEFAULT_RESPONSE_GENERATED)
            .responseTime(DEFAULT_RESPONSE_TIME)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        ChatSession chatSession;
        if (TestUtil.findAll(em, ChatSession.class).isEmpty()) {
            chatSession = ChatSessionResourceIT.createEntity(em);
            em.persist(chatSession);
            em.flush();
        } else {
            chatSession = TestUtil.findAll(em, ChatSession.class).get(0);
        }
        userQuery.setChatSession(chatSession);
        return userQuery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserQuery createUpdatedEntity(EntityManager em) {
        UserQuery updatedUserQuery = new UserQuery()
            .queryText(UPDATED_QUERY_TEXT)
            .queryType(UPDATED_QUERY_TYPE)
            .parameters(UPDATED_PARAMETERS)
            .responseGenerated(UPDATED_RESPONSE_GENERATED)
            .responseTime(UPDATED_RESPONSE_TIME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        ChatSession chatSession;
        if (TestUtil.findAll(em, ChatSession.class).isEmpty()) {
            chatSession = ChatSessionResourceIT.createUpdatedEntity(em);
            em.persist(chatSession);
            em.flush();
        } else {
            chatSession = TestUtil.findAll(em, ChatSession.class).get(0);
        }
        updatedUserQuery.setChatSession(chatSession);
        return updatedUserQuery;
    }

    @BeforeEach
    void initTest() {
        userQuery = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUserQuery != null) {
            userQueryRepository.delete(insertedUserQuery);
            insertedUserQuery = null;
        }
    }

    @Test
    @Transactional
    void createUserQuery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);
        var returnedUserQueryDTO = om.readValue(
            restUserQueryMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userQueryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserQueryDTO.class
        );

        // Validate the UserQuery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserQuery = userQueryMapper.toEntity(returnedUserQueryDTO);
        assertUserQueryUpdatableFieldsEquals(returnedUserQuery, getPersistedUserQuery(returnedUserQuery));

        insertedUserQuery = returnedUserQuery;
    }

    @Test
    @Transactional
    void createUserQueryWithExistingId() throws Exception {
        // Create the UserQuery with an existing ID
        userQuery.setId(1L);
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserQueryMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userQueryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQueryTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userQuery.setQueryText(null);

        // Create the UserQuery, which fails.
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        restUserQueryMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userQueryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQueryTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userQuery.setQueryType(null);

        // Create the UserQuery, which fails.
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        restUserQueryMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userQueryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userQuery.setCreatedAt(null);

        // Create the UserQuery, which fails.
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        restUserQueryMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userQueryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserQueries() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList
        restUserQueryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userQuery.getId().intValue())))
            .andExpect(jsonPath("$.[*].queryText").value(hasItem(DEFAULT_QUERY_TEXT)))
            .andExpect(jsonPath("$.[*].queryType").value(hasItem(DEFAULT_QUERY_TYPE)))
            .andExpect(jsonPath("$.[*].parameters").value(hasItem(DEFAULT_PARAMETERS)))
            .andExpect(jsonPath("$.[*].responseGenerated").value(hasItem(DEFAULT_RESPONSE_GENERATED)))
            .andExpect(jsonPath("$.[*].responseTime").value(hasItem(DEFAULT_RESPONSE_TIME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getUserQuery() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get the userQuery
        restUserQueryMockMvc
            .perform(get(ENTITY_API_URL_ID, userQuery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userQuery.getId().intValue()))
            .andExpect(jsonPath("$.queryText").value(DEFAULT_QUERY_TEXT))
            .andExpect(jsonPath("$.queryType").value(DEFAULT_QUERY_TYPE))
            .andExpect(jsonPath("$.parameters").value(DEFAULT_PARAMETERS))
            .andExpect(jsonPath("$.responseGenerated").value(DEFAULT_RESPONSE_GENERATED))
            .andExpect(jsonPath("$.responseTime").value(DEFAULT_RESPONSE_TIME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getUserQueriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        Long id = userQuery.getId();

        defaultUserQueryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUserQueryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUserQueryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryText equals to
        defaultUserQueryFiltering("queryText.equals=" + DEFAULT_QUERY_TEXT, "queryText.equals=" + UPDATED_QUERY_TEXT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryText in
        defaultUserQueryFiltering("queryText.in=" + DEFAULT_QUERY_TEXT + "," + UPDATED_QUERY_TEXT, "queryText.in=" + UPDATED_QUERY_TEXT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryText is not null
        defaultUserQueryFiltering("queryText.specified=true", "queryText.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTextContainsSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryText contains
        defaultUserQueryFiltering("queryText.contains=" + DEFAULT_QUERY_TEXT, "queryText.contains=" + UPDATED_QUERY_TEXT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryText does not contain
        defaultUserQueryFiltering("queryText.doesNotContain=" + UPDATED_QUERY_TEXT, "queryText.doesNotContain=" + DEFAULT_QUERY_TEXT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryType equals to
        defaultUserQueryFiltering("queryType.equals=" + DEFAULT_QUERY_TYPE, "queryType.equals=" + UPDATED_QUERY_TYPE);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryType in
        defaultUserQueryFiltering("queryType.in=" + DEFAULT_QUERY_TYPE + "," + UPDATED_QUERY_TYPE, "queryType.in=" + UPDATED_QUERY_TYPE);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryType is not null
        defaultUserQueryFiltering("queryType.specified=true", "queryType.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryType contains
        defaultUserQueryFiltering("queryType.contains=" + DEFAULT_QUERY_TYPE, "queryType.contains=" + UPDATED_QUERY_TYPE);
    }

    @Test
    @Transactional
    void getAllUserQueriesByQueryTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where queryType does not contain
        defaultUserQueryFiltering("queryType.doesNotContain=" + UPDATED_QUERY_TYPE, "queryType.doesNotContain=" + DEFAULT_QUERY_TYPE);
    }

    @Test
    @Transactional
    void getAllUserQueriesByParametersIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where parameters equals to
        defaultUserQueryFiltering("parameters.equals=" + DEFAULT_PARAMETERS, "parameters.equals=" + UPDATED_PARAMETERS);
    }

    @Test
    @Transactional
    void getAllUserQueriesByParametersIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where parameters in
        defaultUserQueryFiltering("parameters.in=" + DEFAULT_PARAMETERS + "," + UPDATED_PARAMETERS, "parameters.in=" + UPDATED_PARAMETERS);
    }

    @Test
    @Transactional
    void getAllUserQueriesByParametersIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where parameters is not null
        defaultUserQueryFiltering("parameters.specified=true", "parameters.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByParametersContainsSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where parameters contains
        defaultUserQueryFiltering("parameters.contains=" + DEFAULT_PARAMETERS, "parameters.contains=" + UPDATED_PARAMETERS);
    }

    @Test
    @Transactional
    void getAllUserQueriesByParametersNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where parameters does not contain
        defaultUserQueryFiltering("parameters.doesNotContain=" + UPDATED_PARAMETERS, "parameters.doesNotContain=" + DEFAULT_PARAMETERS);
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseGeneratedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseGenerated equals to
        defaultUserQueryFiltering(
            "responseGenerated.equals=" + DEFAULT_RESPONSE_GENERATED,
            "responseGenerated.equals=" + UPDATED_RESPONSE_GENERATED
        );
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseGeneratedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseGenerated in
        defaultUserQueryFiltering(
            "responseGenerated.in=" + DEFAULT_RESPONSE_GENERATED + "," + UPDATED_RESPONSE_GENERATED,
            "responseGenerated.in=" + UPDATED_RESPONSE_GENERATED
        );
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseGeneratedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseGenerated is not null
        defaultUserQueryFiltering("responseGenerated.specified=true", "responseGenerated.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime equals to
        defaultUserQueryFiltering("responseTime.equals=" + DEFAULT_RESPONSE_TIME, "responseTime.equals=" + UPDATED_RESPONSE_TIME);
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime in
        defaultUserQueryFiltering(
            "responseTime.in=" + DEFAULT_RESPONSE_TIME + "," + UPDATED_RESPONSE_TIME,
            "responseTime.in=" + UPDATED_RESPONSE_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime is not null
        defaultUserQueryFiltering("responseTime.specified=true", "responseTime.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime is greater than or equal to
        defaultUserQueryFiltering(
            "responseTime.greaterThanOrEqual=" + DEFAULT_RESPONSE_TIME,
            "responseTime.greaterThanOrEqual=" + UPDATED_RESPONSE_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime is less than or equal to
        defaultUserQueryFiltering(
            "responseTime.lessThanOrEqual=" + DEFAULT_RESPONSE_TIME,
            "responseTime.lessThanOrEqual=" + SMALLER_RESPONSE_TIME
        );
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime is less than
        defaultUserQueryFiltering("responseTime.lessThan=" + UPDATED_RESPONSE_TIME, "responseTime.lessThan=" + DEFAULT_RESPONSE_TIME);
    }

    @Test
    @Transactional
    void getAllUserQueriesByResponseTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where responseTime is greater than
        defaultUserQueryFiltering("responseTime.greaterThan=" + SMALLER_RESPONSE_TIME, "responseTime.greaterThan=" + DEFAULT_RESPONSE_TIME);
    }

    @Test
    @Transactional
    void getAllUserQueriesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where createdAt equals to
        defaultUserQueryFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where createdAt in
        defaultUserQueryFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where createdAt is not null
        defaultUserQueryFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where updatedAt equals to
        defaultUserQueryFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where updatedAt in
        defaultUserQueryFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where updatedAt is not null
        defaultUserQueryFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where isDeleted equals to
        defaultUserQueryFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllUserQueriesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where isDeleted in
        defaultUserQueryFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllUserQueriesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where isDeleted is not null
        defaultUserQueryFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where deletedAt equals to
        defaultUserQueryFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where deletedAt in
        defaultUserQueryFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserQueriesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where deletedAt is not null
        defaultUserQueryFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where deletedBy equals to
        defaultUserQueryFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllUserQueriesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where deletedBy in
        defaultUserQueryFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllUserQueriesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        // Get all the userQueryList where deletedBy is not null
        defaultUserQueryFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllUserQueriesByChatSessionIsEqualToSomething() throws Exception {
        ChatSession chatSession;
        if (TestUtil.findAll(em, ChatSession.class).isEmpty()) {
            userQueryRepository.saveAndFlush(userQuery);
            chatSession = ChatSessionResourceIT.createEntity(em);
        } else {
            chatSession = TestUtil.findAll(em, ChatSession.class).get(0);
        }
        em.persist(chatSession);
        em.flush();
        userQuery.setChatSession(chatSession);
        userQueryRepository.saveAndFlush(userQuery);
        Long chatSessionId = chatSession.getId();
        // Get all the userQueryList where chatSession equals to chatSessionId
        defaultUserQueryShouldBeFound("chatSessionId.equals=" + chatSessionId);

        // Get all the userQueryList where chatSession equals to (chatSessionId + 1)
        defaultUserQueryShouldNotBeFound("chatSessionId.equals=" + (chatSessionId + 1));
    }

    private void defaultUserQueryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUserQueryShouldBeFound(shouldBeFound);
        defaultUserQueryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserQueryShouldBeFound(String filter) throws Exception {
        restUserQueryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userQuery.getId().intValue())))
            .andExpect(jsonPath("$.[*].queryText").value(hasItem(DEFAULT_QUERY_TEXT)))
            .andExpect(jsonPath("$.[*].queryType").value(hasItem(DEFAULT_QUERY_TYPE)))
            .andExpect(jsonPath("$.[*].parameters").value(hasItem(DEFAULT_PARAMETERS)))
            .andExpect(jsonPath("$.[*].responseGenerated").value(hasItem(DEFAULT_RESPONSE_GENERATED)))
            .andExpect(jsonPath("$.[*].responseTime").value(hasItem(DEFAULT_RESPONSE_TIME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restUserQueryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserQueryShouldNotBeFound(String filter) throws Exception {
        restUserQueryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserQueryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserQuery() throws Exception {
        // Get the userQuery
        restUserQueryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserQuery() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userQuery
        UserQuery updatedUserQuery = userQueryRepository.findById(userQuery.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserQuery are not directly saved in db
        em.detach(updatedUserQuery);
        updatedUserQuery
            .queryText(UPDATED_QUERY_TEXT)
            .queryType(UPDATED_QUERY_TYPE)
            .parameters(UPDATED_PARAMETERS)
            .responseGenerated(UPDATED_RESPONSE_GENERATED)
            .responseTime(UPDATED_RESPONSE_TIME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(updatedUserQuery);

        restUserQueryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userQueryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userQueryDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserQueryToMatchAllProperties(updatedUserQuery);
    }

    @Test
    @Transactional
    void putNonExistingUserQuery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userQuery.setId(longCount.incrementAndGet());

        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserQueryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userQueryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userQueryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserQuery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userQuery.setId(longCount.incrementAndGet());

        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserQueryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userQueryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserQuery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userQuery.setId(longCount.incrementAndGet());

        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserQueryMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userQueryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserQueryWithPatch() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userQuery using partial update
        UserQuery partialUpdatedUserQuery = new UserQuery();
        partialUpdatedUserQuery.setId(userQuery.getId());

        partialUpdatedUserQuery
            .queryText(UPDATED_QUERY_TEXT)
            .queryType(UPDATED_QUERY_TYPE)
            .parameters(UPDATED_PARAMETERS)
            .responseGenerated(UPDATED_RESPONSE_GENERATED)
            .responseTime(UPDATED_RESPONSE_TIME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restUserQueryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserQuery.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserQuery))
            )
            .andExpect(status().isOk());

        // Validate the UserQuery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserQueryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserQuery, userQuery),
            getPersistedUserQuery(userQuery)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserQueryWithPatch() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userQuery using partial update
        UserQuery partialUpdatedUserQuery = new UserQuery();
        partialUpdatedUserQuery.setId(userQuery.getId());

        partialUpdatedUserQuery
            .queryText(UPDATED_QUERY_TEXT)
            .queryType(UPDATED_QUERY_TYPE)
            .parameters(UPDATED_PARAMETERS)
            .responseGenerated(UPDATED_RESPONSE_GENERATED)
            .responseTime(UPDATED_RESPONSE_TIME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restUserQueryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserQuery.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserQuery))
            )
            .andExpect(status().isOk());

        // Validate the UserQuery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserQueryUpdatableFieldsEquals(partialUpdatedUserQuery, getPersistedUserQuery(partialUpdatedUserQuery));
    }

    @Test
    @Transactional
    void patchNonExistingUserQuery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userQuery.setId(longCount.incrementAndGet());

        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserQueryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userQueryDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userQueryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserQuery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userQuery.setId(longCount.incrementAndGet());

        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserQueryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userQueryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserQuery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userQuery.setId(longCount.incrementAndGet());

        // Create the UserQuery
        UserQueryDTO userQueryDTO = userQueryMapper.toDto(userQuery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserQueryMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userQueryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserQuery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserQuery() throws Exception {
        // Initialize the database
        insertedUserQuery = userQueryRepository.saveAndFlush(userQuery);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userQuery
        restUserQueryMockMvc
            .perform(delete(ENTITY_API_URL_ID, userQuery.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userQueryRepository.count();
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

    protected UserQuery getPersistedUserQuery(UserQuery userQuery) {
        return userQueryRepository.findById(userQuery.getId()).orElseThrow();
    }

    protected void assertPersistedUserQueryToMatchAllProperties(UserQuery expectedUserQuery) {
        assertUserQueryAllPropertiesEquals(expectedUserQuery, getPersistedUserQuery(expectedUserQuery));
    }

    protected void assertPersistedUserQueryToMatchUpdatableProperties(UserQuery expectedUserQuery) {
        assertUserQueryAllUpdatablePropertiesEquals(expectedUserQuery, getPersistedUserQuery(expectedUserQuery));
    }
}
