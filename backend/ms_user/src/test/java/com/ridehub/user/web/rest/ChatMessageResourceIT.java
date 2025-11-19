package com.ridehub.user.web.rest;

import static com.ridehub.user.domain.ChatMessageAsserts.*;
import static com.ridehub.user.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.user.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.user.IntegrationTest;
import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.repository.ChatMessageRepository;
import com.ridehub.user.service.dto.ChatMessageDTO;
import com.ridehub.user.service.mapper.ChatMessageMapper;
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
 * Integration tests for the {@link ChatMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChatMessageResourceIT {

    private static final String DEFAULT_MESSAGE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_INTENT = "AAAAAAAAAA";
    private static final String UPDATED_INTENT = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITIES = "AAAAAAAAAA";
    private static final String UPDATED_ENTITIES = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_CONFIDENCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CONFIDENCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_CONFIDENCE = new BigDecimal(1 - 1);

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

    private static final String ENTITY_API_URL = "/api/chat-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChatMessageMockMvc;

    private ChatMessage chatMessage;

    private ChatMessage insertedChatMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatMessage createEntity(EntityManager em) {
        ChatMessage chatMessage = new ChatMessage()
            .messageText(DEFAULT_MESSAGE_TEXT)
            .messageType(DEFAULT_MESSAGE_TYPE)
            .timestamp(DEFAULT_TIMESTAMP)
            .intent(DEFAULT_INTENT)
            .entities(DEFAULT_ENTITIES)
            .confidence(DEFAULT_CONFIDENCE)
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
        chatMessage.setChatSession(chatSession);
        return chatMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatMessage createUpdatedEntity(EntityManager em) {
        ChatMessage updatedChatMessage = new ChatMessage()
            .messageText(UPDATED_MESSAGE_TEXT)
            .messageType(UPDATED_MESSAGE_TYPE)
            .timestamp(UPDATED_TIMESTAMP)
            .intent(UPDATED_INTENT)
            .entities(UPDATED_ENTITIES)
            .confidence(UPDATED_CONFIDENCE)
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
        updatedChatMessage.setChatSession(chatSession);
        return updatedChatMessage;
    }

    @BeforeEach
    void initTest() {
        chatMessage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedChatMessage != null) {
            chatMessageRepository.delete(insertedChatMessage);
            insertedChatMessage = null;
        }
    }

    @Test
    @Transactional
    void createChatMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);
        var returnedChatMessageDTO = om.readValue(
            restChatMessageMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChatMessageDTO.class
        );

        // Validate the ChatMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChatMessage = chatMessageMapper.toEntity(returnedChatMessageDTO);
        assertChatMessageUpdatableFieldsEquals(returnedChatMessage, getPersistedChatMessage(returnedChatMessage));

        insertedChatMessage = returnedChatMessage;
    }

    @Test
    @Transactional
    void createChatMessageWithExistingId() throws Exception {
        // Create the ChatMessage with an existing ID
        chatMessage.setId(1L);
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatMessage.setMessageText(null);

        // Create the ChatMessage, which fails.
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        restChatMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatMessage.setMessageType(null);

        // Create the ChatMessage, which fails.
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        restChatMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatMessage.setTimestamp(null);

        // Create the ChatMessage, which fails.
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        restChatMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chatMessage.setCreatedAt(null);

        // Create the ChatMessage, which fails.
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        restChatMessageMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChatMessages() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList
        restChatMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].messageText").value(hasItem(DEFAULT_MESSAGE_TEXT)))
            .andExpect(jsonPath("$.[*].messageType").value(hasItem(DEFAULT_MESSAGE_TYPE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].intent").value(hasItem(DEFAULT_INTENT)))
            .andExpect(jsonPath("$.[*].entities").value(hasItem(DEFAULT_ENTITIES)))
            .andExpect(jsonPath("$.[*].confidence").value(hasItem(sameNumber(DEFAULT_CONFIDENCE))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getChatMessage() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get the chatMessage
        restChatMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, chatMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chatMessage.getId().intValue()))
            .andExpect(jsonPath("$.messageText").value(DEFAULT_MESSAGE_TEXT))
            .andExpect(jsonPath("$.messageType").value(DEFAULT_MESSAGE_TYPE))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.intent").value(DEFAULT_INTENT))
            .andExpect(jsonPath("$.entities").value(DEFAULT_ENTITIES))
            .andExpect(jsonPath("$.confidence").value(sameNumber(DEFAULT_CONFIDENCE)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getChatMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        Long id = chatMessage.getId();

        defaultChatMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultChatMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultChatMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageText equals to
        defaultChatMessageFiltering("messageText.equals=" + DEFAULT_MESSAGE_TEXT, "messageText.equals=" + UPDATED_MESSAGE_TEXT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageText in
        defaultChatMessageFiltering(
            "messageText.in=" + DEFAULT_MESSAGE_TEXT + "," + UPDATED_MESSAGE_TEXT,
            "messageText.in=" + UPDATED_MESSAGE_TEXT
        );
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageText is not null
        defaultChatMessageFiltering("messageText.specified=true", "messageText.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTextContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageText contains
        defaultChatMessageFiltering("messageText.contains=" + DEFAULT_MESSAGE_TEXT, "messageText.contains=" + UPDATED_MESSAGE_TEXT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageText does not contain
        defaultChatMessageFiltering(
            "messageText.doesNotContain=" + UPDATED_MESSAGE_TEXT,
            "messageText.doesNotContain=" + DEFAULT_MESSAGE_TEXT
        );
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageType equals to
        defaultChatMessageFiltering("messageType.equals=" + DEFAULT_MESSAGE_TYPE, "messageType.equals=" + UPDATED_MESSAGE_TYPE);
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageType in
        defaultChatMessageFiltering(
            "messageType.in=" + DEFAULT_MESSAGE_TYPE + "," + UPDATED_MESSAGE_TYPE,
            "messageType.in=" + UPDATED_MESSAGE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageType is not null
        defaultChatMessageFiltering("messageType.specified=true", "messageType.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageType contains
        defaultChatMessageFiltering("messageType.contains=" + DEFAULT_MESSAGE_TYPE, "messageType.contains=" + UPDATED_MESSAGE_TYPE);
    }

    @Test
    @Transactional
    void getAllChatMessagesByMessageTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where messageType does not contain
        defaultChatMessageFiltering(
            "messageType.doesNotContain=" + UPDATED_MESSAGE_TYPE,
            "messageType.doesNotContain=" + DEFAULT_MESSAGE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllChatMessagesByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where timestamp equals to
        defaultChatMessageFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllChatMessagesByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where timestamp in
        defaultChatMessageFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllChatMessagesByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where timestamp is not null
        defaultChatMessageFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByIntentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where intent equals to
        defaultChatMessageFiltering("intent.equals=" + DEFAULT_INTENT, "intent.equals=" + UPDATED_INTENT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByIntentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where intent in
        defaultChatMessageFiltering("intent.in=" + DEFAULT_INTENT + "," + UPDATED_INTENT, "intent.in=" + UPDATED_INTENT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByIntentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where intent is not null
        defaultChatMessageFiltering("intent.specified=true", "intent.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByIntentContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where intent contains
        defaultChatMessageFiltering("intent.contains=" + DEFAULT_INTENT, "intent.contains=" + UPDATED_INTENT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByIntentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where intent does not contain
        defaultChatMessageFiltering("intent.doesNotContain=" + UPDATED_INTENT, "intent.doesNotContain=" + DEFAULT_INTENT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByEntitiesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where entities equals to
        defaultChatMessageFiltering("entities.equals=" + DEFAULT_ENTITIES, "entities.equals=" + UPDATED_ENTITIES);
    }

    @Test
    @Transactional
    void getAllChatMessagesByEntitiesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where entities in
        defaultChatMessageFiltering("entities.in=" + DEFAULT_ENTITIES + "," + UPDATED_ENTITIES, "entities.in=" + UPDATED_ENTITIES);
    }

    @Test
    @Transactional
    void getAllChatMessagesByEntitiesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where entities is not null
        defaultChatMessageFiltering("entities.specified=true", "entities.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByEntitiesContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where entities contains
        defaultChatMessageFiltering("entities.contains=" + DEFAULT_ENTITIES, "entities.contains=" + UPDATED_ENTITIES);
    }

    @Test
    @Transactional
    void getAllChatMessagesByEntitiesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where entities does not contain
        defaultChatMessageFiltering("entities.doesNotContain=" + UPDATED_ENTITIES, "entities.doesNotContain=" + DEFAULT_ENTITIES);
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence equals to
        defaultChatMessageFiltering("confidence.equals=" + DEFAULT_CONFIDENCE, "confidence.equals=" + UPDATED_CONFIDENCE);
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence in
        defaultChatMessageFiltering(
            "confidence.in=" + DEFAULT_CONFIDENCE + "," + UPDATED_CONFIDENCE,
            "confidence.in=" + UPDATED_CONFIDENCE
        );
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence is not null
        defaultChatMessageFiltering("confidence.specified=true", "confidence.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence is greater than or equal to
        defaultChatMessageFiltering(
            "confidence.greaterThanOrEqual=" + DEFAULT_CONFIDENCE,
            "confidence.greaterThanOrEqual=" + UPDATED_CONFIDENCE
        );
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence is less than or equal to
        defaultChatMessageFiltering("confidence.lessThanOrEqual=" + DEFAULT_CONFIDENCE, "confidence.lessThanOrEqual=" + SMALLER_CONFIDENCE);
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence is less than
        defaultChatMessageFiltering("confidence.lessThan=" + UPDATED_CONFIDENCE, "confidence.lessThan=" + DEFAULT_CONFIDENCE);
    }

    @Test
    @Transactional
    void getAllChatMessagesByConfidenceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where confidence is greater than
        defaultChatMessageFiltering("confidence.greaterThan=" + SMALLER_CONFIDENCE, "confidence.greaterThan=" + DEFAULT_CONFIDENCE);
    }

    @Test
    @Transactional
    void getAllChatMessagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where createdAt equals to
        defaultChatMessageFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where createdAt in
        defaultChatMessageFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where createdAt is not null
        defaultChatMessageFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where updatedAt equals to
        defaultChatMessageFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where updatedAt in
        defaultChatMessageFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where updatedAt is not null
        defaultChatMessageFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where isDeleted equals to
        defaultChatMessageFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllChatMessagesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where isDeleted in
        defaultChatMessageFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllChatMessagesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where isDeleted is not null
        defaultChatMessageFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where deletedAt equals to
        defaultChatMessageFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where deletedAt in
        defaultChatMessageFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllChatMessagesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where deletedAt is not null
        defaultChatMessageFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where deletedBy equals to
        defaultChatMessageFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllChatMessagesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where deletedBy in
        defaultChatMessageFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllChatMessagesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Get all the chatMessageList where deletedBy is not null
        defaultChatMessageFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllChatMessagesByChatSessionIsEqualToSomething() throws Exception {
        ChatSession chatSession;
        if (TestUtil.findAll(em, ChatSession.class).isEmpty()) {
            chatMessageRepository.saveAndFlush(chatMessage);
            chatSession = ChatSessionResourceIT.createEntity(em);
        } else {
            chatSession = TestUtil.findAll(em, ChatSession.class).get(0);
        }
        em.persist(chatSession);
        em.flush();
        chatMessage.setChatSession(chatSession);
        chatMessageRepository.saveAndFlush(chatMessage);
        Long chatSessionId = chatSession.getId();
        // Get all the chatMessageList where chatSession equals to chatSessionId
        defaultChatMessageShouldBeFound("chatSessionId.equals=" + chatSessionId);

        // Get all the chatMessageList where chatSession equals to (chatSessionId + 1)
        defaultChatMessageShouldNotBeFound("chatSessionId.equals=" + (chatSessionId + 1));
    }

    private void defaultChatMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultChatMessageShouldBeFound(shouldBeFound);
        defaultChatMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultChatMessageShouldBeFound(String filter) throws Exception {
        restChatMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].messageText").value(hasItem(DEFAULT_MESSAGE_TEXT)))
            .andExpect(jsonPath("$.[*].messageType").value(hasItem(DEFAULT_MESSAGE_TYPE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].intent").value(hasItem(DEFAULT_INTENT)))
            .andExpect(jsonPath("$.[*].entities").value(hasItem(DEFAULT_ENTITIES)))
            .andExpect(jsonPath("$.[*].confidence").value(hasItem(sameNumber(DEFAULT_CONFIDENCE))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restChatMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultChatMessageShouldNotBeFound(String filter) throws Exception {
        restChatMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChatMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingChatMessage() throws Exception {
        // Get the chatMessage
        restChatMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChatMessage() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatMessage
        ChatMessage updatedChatMessage = chatMessageRepository.findById(chatMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChatMessage are not directly saved in db
        em.detach(updatedChatMessage);
        updatedChatMessage
            .messageText(UPDATED_MESSAGE_TEXT)
            .messageType(UPDATED_MESSAGE_TYPE)
            .timestamp(UPDATED_TIMESTAMP)
            .intent(UPDATED_INTENT)
            .entities(UPDATED_ENTITIES)
            .confidence(UPDATED_CONFIDENCE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(updatedChatMessage);

        restChatMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chatMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChatMessageToMatchAllProperties(updatedChatMessage);
    }

    @Test
    @Transactional
    void putNonExistingChatMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatMessage.setId(longCount.incrementAndGet());

        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chatMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChatMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatMessage.setId(longCount.incrementAndGet());

        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChatMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatMessage.setId(longCount.incrementAndGet());

        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMessageMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChatMessageWithPatch() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatMessage using partial update
        ChatMessage partialUpdatedChatMessage = new ChatMessage();
        partialUpdatedChatMessage.setId(chatMessage.getId());

        partialUpdatedChatMessage
            .timestamp(UPDATED_TIMESTAMP)
            .entities(UPDATED_ENTITIES)
            .confidence(UPDATED_CONFIDENCE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED);

        restChatMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChatMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChatMessage))
            )
            .andExpect(status().isOk());

        // Validate the ChatMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChatMessage, chatMessage),
            getPersistedChatMessage(chatMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateChatMessageWithPatch() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chatMessage using partial update
        ChatMessage partialUpdatedChatMessage = new ChatMessage();
        partialUpdatedChatMessage.setId(chatMessage.getId());

        partialUpdatedChatMessage
            .messageText(UPDATED_MESSAGE_TEXT)
            .messageType(UPDATED_MESSAGE_TYPE)
            .timestamp(UPDATED_TIMESTAMP)
            .intent(UPDATED_INTENT)
            .entities(UPDATED_ENTITIES)
            .confidence(UPDATED_CONFIDENCE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restChatMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChatMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChatMessage))
            )
            .andExpect(status().isOk());

        // Validate the ChatMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatMessageUpdatableFieldsEquals(partialUpdatedChatMessage, getPersistedChatMessage(partialUpdatedChatMessage));
    }

    @Test
    @Transactional
    void patchNonExistingChatMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatMessage.setId(longCount.incrementAndGet());

        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chatMessageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChatMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatMessage.setId(longCount.incrementAndGet());

        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChatMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chatMessage.setId(longCount.incrementAndGet());

        // Create the ChatMessage
        ChatMessageDTO chatMessageDTO = chatMessageMapper.toDto(chatMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMessageMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chatMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChatMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChatMessage() throws Exception {
        // Initialize the database
        insertedChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chatMessage
        restChatMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, chatMessage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chatMessageRepository.count();
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

    protected ChatMessage getPersistedChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.findById(chatMessage.getId()).orElseThrow();
    }

    protected void assertPersistedChatMessageToMatchAllProperties(ChatMessage expectedChatMessage) {
        assertChatMessageAllPropertiesEquals(expectedChatMessage, getPersistedChatMessage(expectedChatMessage));
    }

    protected void assertPersistedChatMessageToMatchUpdatableProperties(ChatMessage expectedChatMessage) {
        assertChatMessageAllUpdatablePropertiesEquals(expectedChatMessage, getPersistedChatMessage(expectedChatMessage));
    }
}
