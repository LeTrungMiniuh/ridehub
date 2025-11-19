package com.ridehub.user.web.rest;

import static com.ridehub.user.domain.ChatSessionAsserts.*;
import static com.ridehub.user.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.user.IntegrationTest;
import com.ridehub.user.domain.AppUser;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.repository.ChatSessionRepository;
import com.ridehub.user.service.dto.ChatSessionDTO;
import com.ridehub.user.service.mapper.ChatSessionMapper;
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
 * Integration tests for the {@link ChatSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChatSessionResourceIT {

    private static final String DEFAULT_SESSION_ID = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ENDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_CONTEXT = "AAAAAAAAAA";
    private static final String UPDATED_CONTEXT = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/chat-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChatSessionMockMvc;

    private ChatSession chatSession;

    private ChatSession insertedChatSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatSession createEntity(EntityManager em) {
        ChatSession chatSession = new ChatSession()
            .sessionId(DEFAULT_SESSION_ID)
            .startedAt(DEFAULT_STARTED_AT)
            .endedAt(DEFAULT_ENDED_AT)
            .isActive(DEFAULT_IS_ACTIVE)
            .context(DEFAULT_CONTEXT)
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
        chatSession.setUser(appUser);
        return chatSession;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatSession createUpdatedEntity(EntityManager em) {
        ChatSession updatedChatSession = new ChatSession()
            .sessionId(UPDATED_SESSION_ID)
            .startedAt(UPDATED_STARTED_AT)
            .endedAt(UPDATED_ENDED_AT)
            .isActive(UPDATED_IS_ACTIVE)
            .context(UPDATED_CONTEXT)
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
        updatedChatSession.setUser(appUser);
        return updatedChatSession;
    }

    @BeforeEach
    void initTest() {
        chatSession = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedChatSession != null) {
            chatSessionRepository.delete(insertedChatSession);
            insertedChatSession = null;
        }
    }

    @Test
    @Transactional
    void createChatSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);
        var returnedChatSessionDTO = om.readValue(
            restChatSessionMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChatSessionDTO.class
        );

        // Validate the ChatSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChatSession = chatSessionMapper.toEntity(returnedChatSessionDTO);
        assertChatSessionUpdatableFieldsEquals(returnedChatSession, getPersistedChatSession(returnedChatSession));

        insertedChatSession = returnedChatSession;
    }

    @Test
    @Transactional
    void createChatSessionWithExistingId() throws Exception {
        // Create the ChatSession with an existing ID
        chatSession.setId(1L);
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSessionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatSession.setSessionId(null);

        // Create the ChatSession, which fails.
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        restChatSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatSession.setStartedAt(null);

        // Create the ChatSession, which fails.
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        restChatSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatSession.setIsActive(null);

        // Create the ChatSession, which fails.
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        restChatSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatSession.setCreatedAt(null);

        // Create the ChatSession, which fails.
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        restChatSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChatSessions() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList
        restChatSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].endedAt").value(hasItem(DEFAULT_ENDED_AT.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].context").value(hasItem(DEFAULT_CONTEXT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getChatSession() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get the chatSession
        restChatSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, chatSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chatSession.getId().intValue()))
            .andExpect(jsonPath("$.sessionId").value(DEFAULT_SESSION_ID))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.endedAt").value(DEFAULT_ENDED_AT.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.context").value(DEFAULT_CONTEXT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getChatSessionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        Long id = chatSession.getId();

        defaultChatSessionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultChatSessionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultChatSessionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllChatSessionsBySessionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where sessionId equals to
        defaultChatSessionFiltering("sessionId.equals=" + DEFAULT_SESSION_ID, "sessionId.equals=" + UPDATED_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllChatSessionsBySessionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where sessionId in
        defaultChatSessionFiltering("sessionId.in=" + DEFAULT_SESSION_ID + "," + UPDATED_SESSION_ID, "sessionId.in=" + UPDATED_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllChatSessionsBySessionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where sessionId is not null
        defaultChatSessionFiltering("sessionId.specified=true", "sessionId.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsBySessionIdContainsSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where sessionId contains
        defaultChatSessionFiltering("sessionId.contains=" + DEFAULT_SESSION_ID, "sessionId.contains=" + UPDATED_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllChatSessionsBySessionIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where sessionId does not contain
        defaultChatSessionFiltering("sessionId.doesNotContain=" + UPDATED_SESSION_ID, "sessionId.doesNotContain=" + DEFAULT_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllChatSessionsByStartedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where startedAt equals to
        defaultChatSessionFiltering("startedAt.equals=" + DEFAULT_STARTED_AT, "startedAt.equals=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByStartedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where startedAt in
        defaultChatSessionFiltering("startedAt.in=" + DEFAULT_STARTED_AT + "," + UPDATED_STARTED_AT, "startedAt.in=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByStartedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where startedAt is not null
        defaultChatSessionFiltering("startedAt.specified=true", "startedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByEndedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where endedAt equals to
        defaultChatSessionFiltering("endedAt.equals=" + DEFAULT_ENDED_AT, "endedAt.equals=" + UPDATED_ENDED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByEndedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where endedAt in
        defaultChatSessionFiltering("endedAt.in=" + DEFAULT_ENDED_AT + "," + UPDATED_ENDED_AT, "endedAt.in=" + UPDATED_ENDED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByEndedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where endedAt is not null
        defaultChatSessionFiltering("endedAt.specified=true", "endedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where isActive equals to
        defaultChatSessionFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllChatSessionsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where isActive in
        defaultChatSessionFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllChatSessionsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where isActive is not null
        defaultChatSessionFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByContextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where context equals to
        defaultChatSessionFiltering("context.equals=" + DEFAULT_CONTEXT, "context.equals=" + UPDATED_CONTEXT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByContextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where context in
        defaultChatSessionFiltering("context.in=" + DEFAULT_CONTEXT + "," + UPDATED_CONTEXT, "context.in=" + UPDATED_CONTEXT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByContextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where context is not null
        defaultChatSessionFiltering("context.specified=true", "context.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByContextContainsSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where context contains
        defaultChatSessionFiltering("context.contains=" + DEFAULT_CONTEXT, "context.contains=" + UPDATED_CONTEXT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByContextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where context does not contain
        defaultChatSessionFiltering("context.doesNotContain=" + UPDATED_CONTEXT, "context.doesNotContain=" + DEFAULT_CONTEXT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where createdAt equals to
        defaultChatSessionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where createdAt in
        defaultChatSessionFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where createdAt is not null
        defaultChatSessionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where updatedAt equals to
        defaultChatSessionFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where updatedAt in
        defaultChatSessionFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where updatedAt is not null
        defaultChatSessionFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where isDeleted equals to
        defaultChatSessionFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllChatSessionsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where isDeleted in
        defaultChatSessionFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllChatSessionsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where isDeleted is not null
        defaultChatSessionFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where deletedAt equals to
        defaultChatSessionFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where deletedAt in
        defaultChatSessionFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllChatSessionsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where deletedAt is not null
        defaultChatSessionFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where deletedBy equals to
        defaultChatSessionFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllChatSessionsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where deletedBy in
        defaultChatSessionFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllChatSessionsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        // Get all the chatSessionList where deletedBy is not null
        defaultChatSessionFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllChatSessionsByUserIsEqualToSomething() throws Exception {
        AppUser user;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            chatSessionRepository.saveAndFlush(chatSession);
            user = AppUserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(user);
        em.flush();
        chatSession.setUser(user);
        chatSessionRepository.saveAndFlush(chatSession);
        Long userId = user.getId();
        // Get all the chatSessionList where user equals to userId
        defaultChatSessionShouldBeFound("userId.equals=" + userId);

        // Get all the chatSessionList where user equals to (userId + 1)
        defaultChatSessionShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultChatSessionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultChatSessionShouldBeFound(shouldBeFound);
        defaultChatSessionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultChatSessionShouldBeFound(String filter) throws Exception {
        restChatSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].endedAt").value(hasItem(DEFAULT_ENDED_AT.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].context").value(hasItem(DEFAULT_CONTEXT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restChatSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultChatSessionShouldNotBeFound(String filter) throws Exception {
        restChatSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChatSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingChatSession() throws Exception {
        // Get the chatSession
        restChatSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChatSession() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatSession
        ChatSession updatedChatSession = chatSessionRepository.findById(chatSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChatSession are not directly saved in db
        em.detach(updatedChatSession);
        updatedChatSession
            .sessionId(UPDATED_SESSION_ID)
            .startedAt(UPDATED_STARTED_AT)
            .endedAt(UPDATED_ENDED_AT)
            .isActive(UPDATED_IS_ACTIVE)
            .context(UPDATED_CONTEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(updatedChatSession);

        restChatSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chatSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChatSessionToMatchAllProperties(updatedChatSession);
    }

    @Test
    @Transactional
    void putNonExistingChatSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatSession.setId(longCount.incrementAndGet());

        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chatSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChatSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatSession.setId(longCount.incrementAndGet());

        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChatSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatSession.setId(longCount.incrementAndGet());

        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatSessionMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChatSessionWithPatch() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatSession using partial update
        ChatSession partialUpdatedChatSession = new ChatSession();
        partialUpdatedChatSession.setId(chatSession.getId());

        partialUpdatedChatSession
            .endedAt(UPDATED_ENDED_AT)
            .context(UPDATED_CONTEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT);

        restChatSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChatSession.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChatSession))
            )
            .andExpect(status().isOk());

        // Validate the ChatSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChatSession, chatSession),
            getPersistedChatSession(chatSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateChatSessionWithPatch() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatSession using partial update
        ChatSession partialUpdatedChatSession = new ChatSession();
        partialUpdatedChatSession.setId(chatSession.getId());

        partialUpdatedChatSession
            .sessionId(UPDATED_SESSION_ID)
            .startedAt(UPDATED_STARTED_AT)
            .endedAt(UPDATED_ENDED_AT)
            .isActive(UPDATED_IS_ACTIVE)
            .context(UPDATED_CONTEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restChatSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChatSession.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChatSession))
            )
            .andExpect(status().isOk());

        // Validate the ChatSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatSessionUpdatableFieldsEquals(partialUpdatedChatSession, getPersistedChatSession(partialUpdatedChatSession));
    }

    @Test
    @Transactional
    void patchNonExistingChatSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatSession.setId(longCount.incrementAndGet());

        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chatSessionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChatSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatSession.setId(longCount.incrementAndGet());

        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChatSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatSession.setId(longCount.incrementAndGet());

        // Create the ChatSession
        ChatSessionDTO chatSessionDTO = chatSessionMapper.toDto(chatSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatSessionMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chatSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChatSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChatSession() throws Exception {
        // Initialize the database
        insertedChatSession = chatSessionRepository.saveAndFlush(chatSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chatSession
        restChatSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, chatSession.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chatSessionRepository.count();
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

    protected ChatSession getPersistedChatSession(ChatSession chatSession) {
        return chatSessionRepository.findById(chatSession.getId()).orElseThrow();
    }

    protected void assertPersistedChatSessionToMatchAllProperties(ChatSession expectedChatSession) {
        assertChatSessionAllPropertiesEquals(expectedChatSession, getPersistedChatSession(expectedChatSession));
    }

    protected void assertPersistedChatSessionToMatchUpdatableProperties(ChatSession expectedChatSession) {
        assertChatSessionAllUpdatablePropertiesEquals(expectedChatSession, getPersistedChatSession(expectedChatSession));
    }
}
