package com.ridehub.booking.web.rest;

import static com.ridehub.booking.domain.PaymentTransactionAsserts.*;
import static com.ridehub.booking.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.booking.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.booking.IntegrationTest;
import com.ridehub.booking.domain.PaymentTransaction;
import com.ridehub.booking.domain.enumeration.PaymentMethod;
import com.ridehub.booking.domain.enumeration.PaymentStatus;
import com.ridehub.booking.repository.PaymentTransactionRepository;
import com.ridehub.booking.service.dto.PaymentTransactionDTO;
import com.ridehub.booking.service.mapper.PaymentTransactionMapper;
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
 * Integration tests for the {@link PaymentTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentTransactionResourceIT {

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ORDER_REF = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_REF = "BBBBBBBBBB";

    private static final PaymentMethod DEFAULT_METHOD = PaymentMethod.VNPAY;
    private static final PaymentMethod UPDATED_METHOD = PaymentMethod.CREDIT_CARD;

    private static final PaymentStatus DEFAULT_STATUS = PaymentStatus.INITIATED;
    private static final PaymentStatus UPDATED_STATUS = PaymentStatus.PROCESSING;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_GATEWAY_CREATE_DATE = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_CREATE_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_GATEWAY_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_NOTE = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/payment-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentTransactionMockMvc;

    private PaymentTransaction paymentTransaction;

    private PaymentTransaction insertedPaymentTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentTransaction createEntity() {
        return new PaymentTransaction()
            .transactionId(DEFAULT_TRANSACTION_ID)
            .orderRef(DEFAULT_ORDER_REF)
            .method(DEFAULT_METHOD)
            .status(DEFAULT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .time(DEFAULT_TIME)
            .gatewayCreateDate(DEFAULT_GATEWAY_CREATE_DATE)
            .gatewayNote(DEFAULT_GATEWAY_NOTE)
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
    public static PaymentTransaction createUpdatedEntity() {
        return new PaymentTransaction()
            .transactionId(UPDATED_TRANSACTION_ID)
            .orderRef(UPDATED_ORDER_REF)
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .time(UPDATED_TIME)
            .gatewayCreateDate(UPDATED_GATEWAY_CREATE_DATE)
            .gatewayNote(UPDATED_GATEWAY_NOTE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        paymentTransaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentTransaction != null) {
            paymentTransactionRepository.delete(insertedPaymentTransaction);
            insertedPaymentTransaction = null;
        }
    }

    @Test
    @Transactional
    void createPaymentTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);
        var returnedPaymentTransactionDTO = om.readValue(
            restPaymentTransactionMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(paymentTransactionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentTransactionDTO.class
        );

        // Validate the PaymentTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentTransaction = paymentTransactionMapper.toEntity(returnedPaymentTransactionDTO);
        assertPaymentTransactionUpdatableFieldsEquals(
            returnedPaymentTransaction,
            getPersistedPaymentTransaction(returnedPaymentTransaction)
        );

        insertedPaymentTransaction = returnedPaymentTransaction;
    }

    @Test
    @Transactional
    void createPaymentTransactionWithExistingId() throws Exception {
        // Create the PaymentTransaction with an existing ID
        paymentTransaction.setId(1L);
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setTransactionId(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setCreatedAt(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentTransactions() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].orderRef").value(hasItem(DEFAULT_ORDER_REF)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].gatewayCreateDate").value(hasItem(DEFAULT_GATEWAY_CREATE_DATE)))
            .andExpect(jsonPath("$.[*].gatewayNote").value(hasItem(DEFAULT_GATEWAY_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getPaymentTransaction() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get the paymentTransaction
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentTransaction.getId().intValue()))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.orderRef").value(DEFAULT_ORDER_REF))
            .andExpect(jsonPath("$.method").value(DEFAULT_METHOD.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()))
            .andExpect(jsonPath("$.gatewayCreateDate").value(DEFAULT_GATEWAY_CREATE_DATE))
            .andExpect(jsonPath("$.gatewayNote").value(DEFAULT_GATEWAY_NOTE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getPaymentTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        Long id = paymentTransaction.getId();

        defaultPaymentTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaymentTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaymentTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTransactionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where transactionId equals to
        defaultPaymentTransactionFiltering(
            "transactionId.equals=" + DEFAULT_TRANSACTION_ID,
            "transactionId.equals=" + UPDATED_TRANSACTION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTransactionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where transactionId in
        defaultPaymentTransactionFiltering(
            "transactionId.in=" + DEFAULT_TRANSACTION_ID + "," + UPDATED_TRANSACTION_ID,
            "transactionId.in=" + UPDATED_TRANSACTION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTransactionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where transactionId is not null
        defaultPaymentTransactionFiltering("transactionId.specified=true", "transactionId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTransactionIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where transactionId contains
        defaultPaymentTransactionFiltering(
            "transactionId.contains=" + DEFAULT_TRANSACTION_ID,
            "transactionId.contains=" + UPDATED_TRANSACTION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTransactionIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where transactionId does not contain
        defaultPaymentTransactionFiltering(
            "transactionId.doesNotContain=" + UPDATED_TRANSACTION_ID,
            "transactionId.doesNotContain=" + DEFAULT_TRANSACTION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByOrderRefIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where orderRef equals to
        defaultPaymentTransactionFiltering("orderRef.equals=" + DEFAULT_ORDER_REF, "orderRef.equals=" + UPDATED_ORDER_REF);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByOrderRefIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where orderRef in
        defaultPaymentTransactionFiltering(
            "orderRef.in=" + DEFAULT_ORDER_REF + "," + UPDATED_ORDER_REF,
            "orderRef.in=" + UPDATED_ORDER_REF
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByOrderRefIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where orderRef is not null
        defaultPaymentTransactionFiltering("orderRef.specified=true", "orderRef.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByOrderRefContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where orderRef contains
        defaultPaymentTransactionFiltering("orderRef.contains=" + DEFAULT_ORDER_REF, "orderRef.contains=" + UPDATED_ORDER_REF);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByOrderRefNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where orderRef does not contain
        defaultPaymentTransactionFiltering("orderRef.doesNotContain=" + UPDATED_ORDER_REF, "orderRef.doesNotContain=" + DEFAULT_ORDER_REF);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where method equals to
        defaultPaymentTransactionFiltering("method.equals=" + DEFAULT_METHOD, "method.equals=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where method in
        defaultPaymentTransactionFiltering("method.in=" + DEFAULT_METHOD + "," + UPDATED_METHOD, "method.in=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where method is not null
        defaultPaymentTransactionFiltering("method.specified=true", "method.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where status equals to
        defaultPaymentTransactionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where status in
        defaultPaymentTransactionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where status is not null
        defaultPaymentTransactionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount equals to
        defaultPaymentTransactionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount in
        defaultPaymentTransactionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount is not null
        defaultPaymentTransactionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount is greater than or equal to
        defaultPaymentTransactionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount is less than or equal to
        defaultPaymentTransactionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount is less than
        defaultPaymentTransactionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where amount is greater than
        defaultPaymentTransactionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where time equals to
        defaultPaymentTransactionFiltering("time.equals=" + DEFAULT_TIME, "time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where time in
        defaultPaymentTransactionFiltering("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME, "time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where time is not null
        defaultPaymentTransactionFiltering("time.specified=true", "time.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayCreateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayCreateDate equals to
        defaultPaymentTransactionFiltering(
            "gatewayCreateDate.equals=" + DEFAULT_GATEWAY_CREATE_DATE,
            "gatewayCreateDate.equals=" + UPDATED_GATEWAY_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayCreateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayCreateDate in
        defaultPaymentTransactionFiltering(
            "gatewayCreateDate.in=" + DEFAULT_GATEWAY_CREATE_DATE + "," + UPDATED_GATEWAY_CREATE_DATE,
            "gatewayCreateDate.in=" + UPDATED_GATEWAY_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayCreateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayCreateDate is not null
        defaultPaymentTransactionFiltering("gatewayCreateDate.specified=true", "gatewayCreateDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayCreateDateContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayCreateDate contains
        defaultPaymentTransactionFiltering(
            "gatewayCreateDate.contains=" + DEFAULT_GATEWAY_CREATE_DATE,
            "gatewayCreateDate.contains=" + UPDATED_GATEWAY_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayCreateDateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayCreateDate does not contain
        defaultPaymentTransactionFiltering(
            "gatewayCreateDate.doesNotContain=" + UPDATED_GATEWAY_CREATE_DATE,
            "gatewayCreateDate.doesNotContain=" + DEFAULT_GATEWAY_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayNote equals to
        defaultPaymentTransactionFiltering("gatewayNote.equals=" + DEFAULT_GATEWAY_NOTE, "gatewayNote.equals=" + UPDATED_GATEWAY_NOTE);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayNote in
        defaultPaymentTransactionFiltering(
            "gatewayNote.in=" + DEFAULT_GATEWAY_NOTE + "," + UPDATED_GATEWAY_NOTE,
            "gatewayNote.in=" + UPDATED_GATEWAY_NOTE
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayNote is not null
        defaultPaymentTransactionFiltering("gatewayNote.specified=true", "gatewayNote.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayNote contains
        defaultPaymentTransactionFiltering("gatewayNote.contains=" + DEFAULT_GATEWAY_NOTE, "gatewayNote.contains=" + UPDATED_GATEWAY_NOTE);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByGatewayNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where gatewayNote does not contain
        defaultPaymentTransactionFiltering(
            "gatewayNote.doesNotContain=" + UPDATED_GATEWAY_NOTE,
            "gatewayNote.doesNotContain=" + DEFAULT_GATEWAY_NOTE
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where createdAt equals to
        defaultPaymentTransactionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where createdAt in
        defaultPaymentTransactionFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where createdAt is not null
        defaultPaymentTransactionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where updatedAt equals to
        defaultPaymentTransactionFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where updatedAt in
        defaultPaymentTransactionFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where updatedAt is not null
        defaultPaymentTransactionFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where isDeleted equals to
        defaultPaymentTransactionFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where isDeleted in
        defaultPaymentTransactionFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where isDeleted is not null
        defaultPaymentTransactionFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where deletedAt equals to
        defaultPaymentTransactionFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where deletedAt in
        defaultPaymentTransactionFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where deletedAt is not null
        defaultPaymentTransactionFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where deletedBy equals to
        defaultPaymentTransactionFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where deletedBy in
        defaultPaymentTransactionFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPaymentTransactionsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList where deletedBy is not null
        defaultPaymentTransactionFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultPaymentTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaymentTransactionShouldBeFound(shouldBeFound);
        defaultPaymentTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentTransactionShouldBeFound(String filter) throws Exception {
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].orderRef").value(hasItem(DEFAULT_ORDER_REF)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].gatewayCreateDate").value(hasItem(DEFAULT_GATEWAY_CREATE_DATE)))
            .andExpect(jsonPath("$.[*].gatewayNote").value(hasItem(DEFAULT_GATEWAY_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentTransactionShouldNotBeFound(String filter) throws Exception {
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaymentTransaction() throws Exception {
        // Get the paymentTransaction
        restPaymentTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentTransaction() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentTransaction
        PaymentTransaction updatedPaymentTransaction = paymentTransactionRepository.findById(paymentTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentTransaction are not directly saved in db
        em.detach(updatedPaymentTransaction);
        updatedPaymentTransaction
            .transactionId(UPDATED_TRANSACTION_ID)
            .orderRef(UPDATED_ORDER_REF)
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .time(UPDATED_TIME)
            .gatewayCreateDate(UPDATED_GATEWAY_CREATE_DATE)
            .gatewayNote(UPDATED_GATEWAY_NOTE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(updatedPaymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentTransactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentTransactionToMatchAllProperties(updatedPaymentTransaction);
    }

    @Test
    @Transactional
    void putNonExistingPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentTransactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentTransaction using partial update
        PaymentTransaction partialUpdatedPaymentTransaction = new PaymentTransaction();
        partialUpdatedPaymentTransaction.setId(paymentTransaction.getId());

        partialUpdatedPaymentTransaction
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentTransaction))
            )
            .andExpect(status().isOk());

        // Validate the PaymentTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentTransaction, paymentTransaction),
            getPersistedPaymentTransaction(paymentTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentTransaction using partial update
        PaymentTransaction partialUpdatedPaymentTransaction = new PaymentTransaction();
        partialUpdatedPaymentTransaction.setId(paymentTransaction.getId());

        partialUpdatedPaymentTransaction
            .transactionId(UPDATED_TRANSACTION_ID)
            .orderRef(UPDATED_ORDER_REF)
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .time(UPDATED_TIME)
            .gatewayCreateDate(UPDATED_GATEWAY_CREATE_DATE)
            .gatewayNote(UPDATED_GATEWAY_NOTE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentTransaction))
            )
            .andExpect(status().isOk());

        // Validate the PaymentTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentTransactionUpdatableFieldsEquals(
            partialUpdatedPaymentTransaction,
            getPersistedPaymentTransaction(partialUpdatedPaymentTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentTransactionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentTransaction() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentTransaction
        restPaymentTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentTransaction.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentTransactionRepository.count();
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

    protected PaymentTransaction getPersistedPaymentTransaction(PaymentTransaction paymentTransaction) {
        return paymentTransactionRepository.findById(paymentTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentTransactionToMatchAllProperties(PaymentTransaction expectedPaymentTransaction) {
        assertPaymentTransactionAllPropertiesEquals(expectedPaymentTransaction, getPersistedPaymentTransaction(expectedPaymentTransaction));
    }

    protected void assertPersistedPaymentTransactionToMatchUpdatableProperties(PaymentTransaction expectedPaymentTransaction) {
        assertPaymentTransactionAllUpdatablePropertiesEquals(
            expectedPaymentTransaction,
            getPersistedPaymentTransaction(expectedPaymentTransaction)
        );
    }
}
