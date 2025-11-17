package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.PricingTemplateAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.route.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.PricingTemplate;
import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.enumeration.OccasionType;
import com.ridehub.route.domain.enumeration.SeatType;
import com.ridehub.route.domain.enumeration.VehicleType;
import com.ridehub.route.repository.PricingTemplateRepository;
import com.ridehub.route.service.dto.PricingTemplateDTO;
import com.ridehub.route.service.mapper.PricingTemplateMapper;
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
 * Integration tests for the {@link PricingTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PricingTemplateResourceIT {

    private static final VehicleType DEFAULT_VEHICLE_TYPE = VehicleType.STANDARD_BUS_VIP;
    private static final VehicleType UPDATED_VEHICLE_TYPE = VehicleType.STANDARD_BUS_NORMAL;

    private static final SeatType DEFAULT_SEAT_TYPE = SeatType.SLEEPER;
    private static final SeatType UPDATED_SEAT_TYPE = SeatType.NORMAL;

    private static final OccasionType DEFAULT_OCCASION_TYPE = OccasionType.NORMAL;
    private static final OccasionType UPDATED_OCCASION_TYPE = OccasionType.WEEKEND;

    private static final BigDecimal DEFAULT_BASE_FARE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BASE_FARE = new BigDecimal(2);
    private static final BigDecimal SMALLER_BASE_FARE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_VEHICLE_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_VEHICLE_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_VEHICLE_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_FLOOR_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_FLOOR_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_FLOOR_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_SEAT_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_SEAT_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_SEAT_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_OCCASION_FACTOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_OCCASION_FACTOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_OCCASION_FACTOR = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_FINAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_FINAL_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_FINAL_PRICE = new BigDecimal(1 - 1);

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

    private static final String ENTITY_API_URL = "/api/pricing-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PricingTemplateRepository pricingTemplateRepository;

    @Autowired
    private PricingTemplateMapper pricingTemplateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPricingTemplateMockMvc;

    private PricingTemplate pricingTemplate;

    private PricingTemplate insertedPricingTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PricingTemplate createEntity(EntityManager em) {
        PricingTemplate pricingTemplate = new PricingTemplate()
            .vehicleType(DEFAULT_VEHICLE_TYPE)
            .seatType(DEFAULT_SEAT_TYPE)
            .occasionType(DEFAULT_OCCASION_TYPE)
            .baseFare(DEFAULT_BASE_FARE)
            .vehicleFactor(DEFAULT_VEHICLE_FACTOR)
            .floorFactor(DEFAULT_FLOOR_FACTOR)
            .seatFactor(DEFAULT_SEAT_FACTOR)
            .occasionFactor(DEFAULT_OCCASION_FACTOR)
            .finalPrice(DEFAULT_FINAL_PRICE)
            .validFrom(DEFAULT_VALID_FROM)
            .validTo(DEFAULT_VALID_TO)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            route = RouteResourceIT.createEntity(em);
            em.persist(route);
            em.flush();
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        pricingTemplate.setRoute(route);
        return pricingTemplate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PricingTemplate createUpdatedEntity(EntityManager em) {
        PricingTemplate updatedPricingTemplate = new PricingTemplate()
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .seatType(UPDATED_SEAT_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .baseFare(UPDATED_BASE_FARE)
            .vehicleFactor(UPDATED_VEHICLE_FACTOR)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            route = RouteResourceIT.createUpdatedEntity(em);
            em.persist(route);
            em.flush();
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        updatedPricingTemplate.setRoute(route);
        return updatedPricingTemplate;
    }

    @BeforeEach
    void initTest() {
        pricingTemplate = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPricingTemplate != null) {
            pricingTemplateRepository.delete(insertedPricingTemplate);
            insertedPricingTemplate = null;
        }
    }

    @Test
    @Transactional
    void createPricingTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);
        var returnedPricingTemplateDTO = om.readValue(
            restPricingTemplateMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(pricingTemplateDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PricingTemplateDTO.class
        );

        // Validate the PricingTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPricingTemplate = pricingTemplateMapper.toEntity(returnedPricingTemplateDTO);
        assertPricingTemplateUpdatableFieldsEquals(returnedPricingTemplate, getPersistedPricingTemplate(returnedPricingTemplate));

        insertedPricingTemplate = returnedPricingTemplate;
    }

    @Test
    @Transactional
    void createPricingTemplateWithExistingId() throws Exception {
        // Create the PricingTemplate with an existing ID
        pricingTemplate.setId(1L);
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVehicleTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setVehicleType(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeatTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setSeatType(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOccasionTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setOccasionType(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBaseFareIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setBaseFare(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOccasionFactorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setOccasionFactor(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFinalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setFinalPrice(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setValidFrom(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pricingTemplate.setCreatedAt(null);

        // Create the PricingTemplate, which fails.
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPricingTemplates() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList
        restPricingTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pricingTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].vehicleType").value(hasItem(DEFAULT_VEHICLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].seatType").value(hasItem(DEFAULT_SEAT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].occasionType").value(hasItem(DEFAULT_OCCASION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].baseFare").value(hasItem(sameNumber(DEFAULT_BASE_FARE))))
            .andExpect(jsonPath("$.[*].vehicleFactor").value(hasItem(sameNumber(DEFAULT_VEHICLE_FACTOR))))
            .andExpect(jsonPath("$.[*].floorFactor").value(hasItem(sameNumber(DEFAULT_FLOOR_FACTOR))))
            .andExpect(jsonPath("$.[*].seatFactor").value(hasItem(sameNumber(DEFAULT_SEAT_FACTOR))))
            .andExpect(jsonPath("$.[*].occasionFactor").value(hasItem(sameNumber(DEFAULT_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(sameNumber(DEFAULT_FINAL_PRICE))))
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
    void getPricingTemplate() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get the pricingTemplate
        restPricingTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, pricingTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pricingTemplate.getId().intValue()))
            .andExpect(jsonPath("$.vehicleType").value(DEFAULT_VEHICLE_TYPE.toString()))
            .andExpect(jsonPath("$.seatType").value(DEFAULT_SEAT_TYPE.toString()))
            .andExpect(jsonPath("$.occasionType").value(DEFAULT_OCCASION_TYPE.toString()))
            .andExpect(jsonPath("$.baseFare").value(sameNumber(DEFAULT_BASE_FARE)))
            .andExpect(jsonPath("$.vehicleFactor").value(sameNumber(DEFAULT_VEHICLE_FACTOR)))
            .andExpect(jsonPath("$.floorFactor").value(sameNumber(DEFAULT_FLOOR_FACTOR)))
            .andExpect(jsonPath("$.seatFactor").value(sameNumber(DEFAULT_SEAT_FACTOR)))
            .andExpect(jsonPath("$.occasionFactor").value(sameNumber(DEFAULT_OCCASION_FACTOR)))
            .andExpect(jsonPath("$.finalPrice").value(sameNumber(DEFAULT_FINAL_PRICE)))
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
    void getPricingTemplatesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        Long id = pricingTemplate.getId();

        defaultPricingTemplateFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPricingTemplateFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPricingTemplateFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleType equals to
        defaultPricingTemplateFiltering("vehicleType.equals=" + DEFAULT_VEHICLE_TYPE, "vehicleType.equals=" + UPDATED_VEHICLE_TYPE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleType in
        defaultPricingTemplateFiltering(
            "vehicleType.in=" + DEFAULT_VEHICLE_TYPE + "," + UPDATED_VEHICLE_TYPE,
            "vehicleType.in=" + UPDATED_VEHICLE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleType is not null
        defaultPricingTemplateFiltering("vehicleType.specified=true", "vehicleType.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatType equals to
        defaultPricingTemplateFiltering("seatType.equals=" + DEFAULT_SEAT_TYPE, "seatType.equals=" + UPDATED_SEAT_TYPE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatType in
        defaultPricingTemplateFiltering("seatType.in=" + DEFAULT_SEAT_TYPE + "," + UPDATED_SEAT_TYPE, "seatType.in=" + UPDATED_SEAT_TYPE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatType is not null
        defaultPricingTemplateFiltering("seatType.specified=true", "seatType.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionType equals to
        defaultPricingTemplateFiltering("occasionType.equals=" + DEFAULT_OCCASION_TYPE, "occasionType.equals=" + UPDATED_OCCASION_TYPE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionType in
        defaultPricingTemplateFiltering(
            "occasionType.in=" + DEFAULT_OCCASION_TYPE + "," + UPDATED_OCCASION_TYPE,
            "occasionType.in=" + UPDATED_OCCASION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionType is not null
        defaultPricingTemplateFiltering("occasionType.specified=true", "occasionType.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare equals to
        defaultPricingTemplateFiltering("baseFare.equals=" + DEFAULT_BASE_FARE, "baseFare.equals=" + UPDATED_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare in
        defaultPricingTemplateFiltering("baseFare.in=" + DEFAULT_BASE_FARE + "," + UPDATED_BASE_FARE, "baseFare.in=" + UPDATED_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare is not null
        defaultPricingTemplateFiltering("baseFare.specified=true", "baseFare.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare is greater than or equal to
        defaultPricingTemplateFiltering(
            "baseFare.greaterThanOrEqual=" + DEFAULT_BASE_FARE,
            "baseFare.greaterThanOrEqual=" + UPDATED_BASE_FARE
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare is less than or equal to
        defaultPricingTemplateFiltering("baseFare.lessThanOrEqual=" + DEFAULT_BASE_FARE, "baseFare.lessThanOrEqual=" + SMALLER_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare is less than
        defaultPricingTemplateFiltering("baseFare.lessThan=" + UPDATED_BASE_FARE, "baseFare.lessThan=" + DEFAULT_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByBaseFareIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where baseFare is greater than
        defaultPricingTemplateFiltering("baseFare.greaterThan=" + SMALLER_BASE_FARE, "baseFare.greaterThan=" + DEFAULT_BASE_FARE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor equals to
        defaultPricingTemplateFiltering("vehicleFactor.equals=" + DEFAULT_VEHICLE_FACTOR, "vehicleFactor.equals=" + UPDATED_VEHICLE_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor in
        defaultPricingTemplateFiltering(
            "vehicleFactor.in=" + DEFAULT_VEHICLE_FACTOR + "," + UPDATED_VEHICLE_FACTOR,
            "vehicleFactor.in=" + UPDATED_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor is not null
        defaultPricingTemplateFiltering("vehicleFactor.specified=true", "vehicleFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor is greater than or equal to
        defaultPricingTemplateFiltering(
            "vehicleFactor.greaterThanOrEqual=" + DEFAULT_VEHICLE_FACTOR,
            "vehicleFactor.greaterThanOrEqual=" + UPDATED_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor is less than or equal to
        defaultPricingTemplateFiltering(
            "vehicleFactor.lessThanOrEqual=" + DEFAULT_VEHICLE_FACTOR,
            "vehicleFactor.lessThanOrEqual=" + SMALLER_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor is less than
        defaultPricingTemplateFiltering(
            "vehicleFactor.lessThan=" + UPDATED_VEHICLE_FACTOR,
            "vehicleFactor.lessThan=" + DEFAULT_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByVehicleFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where vehicleFactor is greater than
        defaultPricingTemplateFiltering(
            "vehicleFactor.greaterThan=" + SMALLER_VEHICLE_FACTOR,
            "vehicleFactor.greaterThan=" + DEFAULT_VEHICLE_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor equals to
        defaultPricingTemplateFiltering("floorFactor.equals=" + DEFAULT_FLOOR_FACTOR, "floorFactor.equals=" + UPDATED_FLOOR_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor in
        defaultPricingTemplateFiltering(
            "floorFactor.in=" + DEFAULT_FLOOR_FACTOR + "," + UPDATED_FLOOR_FACTOR,
            "floorFactor.in=" + UPDATED_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor is not null
        defaultPricingTemplateFiltering("floorFactor.specified=true", "floorFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor is greater than or equal to
        defaultPricingTemplateFiltering(
            "floorFactor.greaterThanOrEqual=" + DEFAULT_FLOOR_FACTOR,
            "floorFactor.greaterThanOrEqual=" + UPDATED_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor is less than or equal to
        defaultPricingTemplateFiltering(
            "floorFactor.lessThanOrEqual=" + DEFAULT_FLOOR_FACTOR,
            "floorFactor.lessThanOrEqual=" + SMALLER_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor is less than
        defaultPricingTemplateFiltering("floorFactor.lessThan=" + UPDATED_FLOOR_FACTOR, "floorFactor.lessThan=" + DEFAULT_FLOOR_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFloorFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where floorFactor is greater than
        defaultPricingTemplateFiltering(
            "floorFactor.greaterThan=" + SMALLER_FLOOR_FACTOR,
            "floorFactor.greaterThan=" + DEFAULT_FLOOR_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor equals to
        defaultPricingTemplateFiltering("seatFactor.equals=" + DEFAULT_SEAT_FACTOR, "seatFactor.equals=" + UPDATED_SEAT_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor in
        defaultPricingTemplateFiltering(
            "seatFactor.in=" + DEFAULT_SEAT_FACTOR + "," + UPDATED_SEAT_FACTOR,
            "seatFactor.in=" + UPDATED_SEAT_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor is not null
        defaultPricingTemplateFiltering("seatFactor.specified=true", "seatFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor is greater than or equal to
        defaultPricingTemplateFiltering(
            "seatFactor.greaterThanOrEqual=" + DEFAULT_SEAT_FACTOR,
            "seatFactor.greaterThanOrEqual=" + UPDATED_SEAT_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor is less than or equal to
        defaultPricingTemplateFiltering(
            "seatFactor.lessThanOrEqual=" + DEFAULT_SEAT_FACTOR,
            "seatFactor.lessThanOrEqual=" + SMALLER_SEAT_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor is less than
        defaultPricingTemplateFiltering("seatFactor.lessThan=" + UPDATED_SEAT_FACTOR, "seatFactor.lessThan=" + DEFAULT_SEAT_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesBySeatFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where seatFactor is greater than
        defaultPricingTemplateFiltering("seatFactor.greaterThan=" + SMALLER_SEAT_FACTOR, "seatFactor.greaterThan=" + DEFAULT_SEAT_FACTOR);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor equals to
        defaultPricingTemplateFiltering(
            "occasionFactor.equals=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.equals=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor in
        defaultPricingTemplateFiltering(
            "occasionFactor.in=" + DEFAULT_OCCASION_FACTOR + "," + UPDATED_OCCASION_FACTOR,
            "occasionFactor.in=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor is not null
        defaultPricingTemplateFiltering("occasionFactor.specified=true", "occasionFactor.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor is greater than or equal to
        defaultPricingTemplateFiltering(
            "occasionFactor.greaterThanOrEqual=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.greaterThanOrEqual=" + UPDATED_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor is less than or equal to
        defaultPricingTemplateFiltering(
            "occasionFactor.lessThanOrEqual=" + DEFAULT_OCCASION_FACTOR,
            "occasionFactor.lessThanOrEqual=" + SMALLER_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor is less than
        defaultPricingTemplateFiltering(
            "occasionFactor.lessThan=" + UPDATED_OCCASION_FACTOR,
            "occasionFactor.lessThan=" + DEFAULT_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByOccasionFactorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where occasionFactor is greater than
        defaultPricingTemplateFiltering(
            "occasionFactor.greaterThan=" + SMALLER_OCCASION_FACTOR,
            "occasionFactor.greaterThan=" + DEFAULT_OCCASION_FACTOR
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice equals to
        defaultPricingTemplateFiltering("finalPrice.equals=" + DEFAULT_FINAL_PRICE, "finalPrice.equals=" + UPDATED_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice in
        defaultPricingTemplateFiltering(
            "finalPrice.in=" + DEFAULT_FINAL_PRICE + "," + UPDATED_FINAL_PRICE,
            "finalPrice.in=" + UPDATED_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice is not null
        defaultPricingTemplateFiltering("finalPrice.specified=true", "finalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice is greater than or equal to
        defaultPricingTemplateFiltering(
            "finalPrice.greaterThanOrEqual=" + DEFAULT_FINAL_PRICE,
            "finalPrice.greaterThanOrEqual=" + UPDATED_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice is less than or equal to
        defaultPricingTemplateFiltering(
            "finalPrice.lessThanOrEqual=" + DEFAULT_FINAL_PRICE,
            "finalPrice.lessThanOrEqual=" + SMALLER_FINAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice is less than
        defaultPricingTemplateFiltering("finalPrice.lessThan=" + UPDATED_FINAL_PRICE, "finalPrice.lessThan=" + DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByFinalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where finalPrice is greater than
        defaultPricingTemplateFiltering("finalPrice.greaterThan=" + SMALLER_FINAL_PRICE, "finalPrice.greaterThan=" + DEFAULT_FINAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom equals to
        defaultPricingTemplateFiltering("validFrom.equals=" + DEFAULT_VALID_FROM, "validFrom.equals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom in
        defaultPricingTemplateFiltering(
            "validFrom.in=" + DEFAULT_VALID_FROM + "," + UPDATED_VALID_FROM,
            "validFrom.in=" + UPDATED_VALID_FROM
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom is not null
        defaultPricingTemplateFiltering("validFrom.specified=true", "validFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom is greater than or equal to
        defaultPricingTemplateFiltering(
            "validFrom.greaterThanOrEqual=" + DEFAULT_VALID_FROM,
            "validFrom.greaterThanOrEqual=" + UPDATED_VALID_FROM
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom is less than or equal to
        defaultPricingTemplateFiltering(
            "validFrom.lessThanOrEqual=" + DEFAULT_VALID_FROM,
            "validFrom.lessThanOrEqual=" + SMALLER_VALID_FROM
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom is less than
        defaultPricingTemplateFiltering("validFrom.lessThan=" + UPDATED_VALID_FROM, "validFrom.lessThan=" + DEFAULT_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidFromIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validFrom is greater than
        defaultPricingTemplateFiltering("validFrom.greaterThan=" + SMALLER_VALID_FROM, "validFrom.greaterThan=" + DEFAULT_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo equals to
        defaultPricingTemplateFiltering("validTo.equals=" + DEFAULT_VALID_TO, "validTo.equals=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo in
        defaultPricingTemplateFiltering("validTo.in=" + DEFAULT_VALID_TO + "," + UPDATED_VALID_TO, "validTo.in=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo is not null
        defaultPricingTemplateFiltering("validTo.specified=true", "validTo.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo is greater than or equal to
        defaultPricingTemplateFiltering("validTo.greaterThanOrEqual=" + DEFAULT_VALID_TO, "validTo.greaterThanOrEqual=" + UPDATED_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo is less than or equal to
        defaultPricingTemplateFiltering("validTo.lessThanOrEqual=" + DEFAULT_VALID_TO, "validTo.lessThanOrEqual=" + SMALLER_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo is less than
        defaultPricingTemplateFiltering("validTo.lessThan=" + UPDATED_VALID_TO, "validTo.lessThan=" + DEFAULT_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByValidToIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where validTo is greater than
        defaultPricingTemplateFiltering("validTo.greaterThan=" + SMALLER_VALID_TO, "validTo.greaterThan=" + DEFAULT_VALID_TO);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where createdAt equals to
        defaultPricingTemplateFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where createdAt in
        defaultPricingTemplateFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where createdAt is not null
        defaultPricingTemplateFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where updatedAt equals to
        defaultPricingTemplateFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where updatedAt in
        defaultPricingTemplateFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where updatedAt is not null
        defaultPricingTemplateFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where isDeleted equals to
        defaultPricingTemplateFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where isDeleted in
        defaultPricingTemplateFiltering(
            "isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED,
            "isDeleted.in=" + UPDATED_IS_DELETED
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where isDeleted is not null
        defaultPricingTemplateFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where deletedAt equals to
        defaultPricingTemplateFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where deletedAt in
        defaultPricingTemplateFiltering(
            "deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT,
            "deletedAt.in=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where deletedAt is not null
        defaultPricingTemplateFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where deletedBy equals to
        defaultPricingTemplateFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where deletedBy in
        defaultPricingTemplateFiltering(
            "deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY,
            "deletedBy.in=" + UPDATED_DELETED_BY
        );
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        // Get all the pricingTemplateList where deletedBy is not null
        defaultPricingTemplateFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPricingTemplatesByRouteIsEqualToSomething() throws Exception {
        Route route;
        if (TestUtil.findAll(em, Route.class).isEmpty()) {
            pricingTemplateRepository.saveAndFlush(pricingTemplate);
            route = RouteResourceIT.createEntity(em);
        } else {
            route = TestUtil.findAll(em, Route.class).get(0);
        }
        em.persist(route);
        em.flush();
        pricingTemplate.setRoute(route);
        pricingTemplateRepository.saveAndFlush(pricingTemplate);
        Long routeId = route.getId();
        // Get all the pricingTemplateList where route equals to routeId
        defaultPricingTemplateShouldBeFound("routeId.equals=" + routeId);

        // Get all the pricingTemplateList where route equals to (routeId + 1)
        defaultPricingTemplateShouldNotBeFound("routeId.equals=" + (routeId + 1));
    }

    private void defaultPricingTemplateFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPricingTemplateShouldBeFound(shouldBeFound);
        defaultPricingTemplateShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPricingTemplateShouldBeFound(String filter) throws Exception {
        restPricingTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pricingTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].vehicleType").value(hasItem(DEFAULT_VEHICLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].seatType").value(hasItem(DEFAULT_SEAT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].occasionType").value(hasItem(DEFAULT_OCCASION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].baseFare").value(hasItem(sameNumber(DEFAULT_BASE_FARE))))
            .andExpect(jsonPath("$.[*].vehicleFactor").value(hasItem(sameNumber(DEFAULT_VEHICLE_FACTOR))))
            .andExpect(jsonPath("$.[*].floorFactor").value(hasItem(sameNumber(DEFAULT_FLOOR_FACTOR))))
            .andExpect(jsonPath("$.[*].seatFactor").value(hasItem(sameNumber(DEFAULT_SEAT_FACTOR))))
            .andExpect(jsonPath("$.[*].occasionFactor").value(hasItem(sameNumber(DEFAULT_OCCASION_FACTOR))))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(sameNumber(DEFAULT_FINAL_PRICE))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restPricingTemplateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPricingTemplateShouldNotBeFound(String filter) throws Exception {
        restPricingTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPricingTemplateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPricingTemplate() throws Exception {
        // Get the pricingTemplate
        restPricingTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPricingTemplate() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricingTemplate
        PricingTemplate updatedPricingTemplate = pricingTemplateRepository.findById(pricingTemplate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPricingTemplate are not directly saved in db
        em.detach(updatedPricingTemplate);
        updatedPricingTemplate
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .seatType(UPDATED_SEAT_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .baseFare(UPDATED_BASE_FARE)
            .vehicleFactor(UPDATED_VEHICLE_FACTOR)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(updatedPricingTemplate);

        restPricingTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pricingTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPricingTemplateToMatchAllProperties(updatedPricingTemplate);
    }

    @Test
    @Transactional
    void putNonExistingPricingTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingTemplate.setId(longCount.incrementAndGet());

        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPricingTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pricingTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPricingTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingTemplate.setId(longCount.incrementAndGet());

        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPricingTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingTemplate.setId(longCount.incrementAndGet());

        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingTemplateMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePricingTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricingTemplate using partial update
        PricingTemplate partialUpdatedPricingTemplate = new PricingTemplate();
        partialUpdatedPricingTemplate.setId(pricingTemplate.getId());

        partialUpdatedPricingTemplate
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT);

        restPricingTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPricingTemplate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPricingTemplate))
            )
            .andExpect(status().isOk());

        // Validate the PricingTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPricingTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPricingTemplate, pricingTemplate),
            getPersistedPricingTemplate(pricingTemplate)
        );
    }

    @Test
    @Transactional
    void fullUpdatePricingTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pricingTemplate using partial update
        PricingTemplate partialUpdatedPricingTemplate = new PricingTemplate();
        partialUpdatedPricingTemplate.setId(pricingTemplate.getId());

        partialUpdatedPricingTemplate
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .seatType(UPDATED_SEAT_TYPE)
            .occasionType(UPDATED_OCCASION_TYPE)
            .baseFare(UPDATED_BASE_FARE)
            .vehicleFactor(UPDATED_VEHICLE_FACTOR)
            .floorFactor(UPDATED_FLOOR_FACTOR)
            .seatFactor(UPDATED_SEAT_FACTOR)
            .occasionFactor(UPDATED_OCCASION_FACTOR)
            .finalPrice(UPDATED_FINAL_PRICE)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restPricingTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPricingTemplate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPricingTemplate))
            )
            .andExpect(status().isOk());

        // Validate the PricingTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPricingTemplateUpdatableFieldsEquals(
            partialUpdatedPricingTemplate,
            getPersistedPricingTemplate(partialUpdatedPricingTemplate)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPricingTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingTemplate.setId(longCount.incrementAndGet());

        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPricingTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pricingTemplateDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPricingTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingTemplate.setId(longCount.incrementAndGet());

        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPricingTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pricingTemplate.setId(longCount.incrementAndGet());

        // Create the PricingTemplate
        PricingTemplateDTO pricingTemplateDTO = pricingTemplateMapper.toDto(pricingTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPricingTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pricingTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PricingTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePricingTemplate() throws Exception {
        // Initialize the database
        insertedPricingTemplate = pricingTemplateRepository.saveAndFlush(pricingTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pricingTemplate
        restPricingTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, pricingTemplate.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pricingTemplateRepository.count();
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

    protected PricingTemplate getPersistedPricingTemplate(PricingTemplate pricingTemplate) {
        return pricingTemplateRepository.findById(pricingTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedPricingTemplateToMatchAllProperties(PricingTemplate expectedPricingTemplate) {
        assertPricingTemplateAllPropertiesEquals(expectedPricingTemplate, getPersistedPricingTemplate(expectedPricingTemplate));
    }

    protected void assertPersistedPricingTemplateToMatchUpdatableProperties(PricingTemplate expectedPricingTemplate) {
        assertPricingTemplateAllUpdatablePropertiesEquals(expectedPricingTemplate, getPersistedPricingTemplate(expectedPricingTemplate));
    }
}
