package com.ridehub.user.web.rest;

import com.ridehub.user.repository.UserQueryRepository;
import com.ridehub.user.service.UserQueryQueryService;
import com.ridehub.user.service.UserQueryService;
import com.ridehub.user.service.criteria.UserQueryCriteria;
import com.ridehub.user.service.dto.UserQueryDTO;
import com.ridehub.user.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.user.domain.UserQuery}.
 */
@RestController
@RequestMapping("/api/user-queries")
public class UserQueryResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserQueryResource.class);

    private static final String ENTITY_NAME = "msUserUserQuery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserQueryService userQueryService;

    private final UserQueryRepository userQueryRepository;

    private final UserQueryQueryService userQueryQueryService;

    public UserQueryResource(
        UserQueryService userQueryService,
        UserQueryRepository userQueryRepository,
        UserQueryQueryService userQueryQueryService
    ) {
        this.userQueryService = userQueryService;
        this.userQueryRepository = userQueryRepository;
        this.userQueryQueryService = userQueryQueryService;
    }

    /**
     * {@code POST  /user-queries} : Create a new userQuery.
     *
     * @param userQueryDTO the userQueryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userQueryDTO, or with status {@code 400 (Bad Request)} if the userQuery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserQueryDTO> createUserQuery(@Valid @RequestBody UserQueryDTO userQueryDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserQuery : {}", userQueryDTO);
        if (userQueryDTO.getId() != null) {
            throw new BadRequestAlertException("A new userQuery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userQueryDTO = userQueryService.save(userQueryDTO);
        return ResponseEntity.created(new URI("/api/user-queries/" + userQueryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userQueryDTO.getId().toString()))
            .body(userQueryDTO);
    }

    /**
     * {@code PUT  /user-queries/:id} : Updates an existing userQuery.
     *
     * @param id the id of the userQueryDTO to save.
     * @param userQueryDTO the userQueryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userQueryDTO,
     * or with status {@code 400 (Bad Request)} if the userQueryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userQueryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserQueryDTO> updateUserQuery(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserQueryDTO userQueryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserQuery : {}, {}", id, userQueryDTO);
        if (userQueryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userQueryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userQueryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userQueryDTO = userQueryService.update(userQueryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userQueryDTO.getId().toString()))
            .body(userQueryDTO);
    }

    /**
     * {@code PATCH  /user-queries/:id} : Partial updates given fields of an existing userQuery, field will ignore if it is null
     *
     * @param id the id of the userQueryDTO to save.
     * @param userQueryDTO the userQueryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userQueryDTO,
     * or with status {@code 400 (Bad Request)} if the userQueryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userQueryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userQueryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserQueryDTO> partialUpdateUserQuery(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserQueryDTO userQueryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserQuery partially : {}, {}", id, userQueryDTO);
        if (userQueryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userQueryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userQueryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserQueryDTO> result = userQueryService.partialUpdate(userQueryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userQueryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-queries} : get all the userQueries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userQueries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserQueryDTO>> getAllUserQueries(UserQueryCriteria criteria) {
        LOG.debug("REST request to get UserQueries by criteria: {}", criteria);

        List<UserQueryDTO> entityList = userQueryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-queries/count} : count all the userQueries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUserQueries(UserQueryCriteria criteria) {
        LOG.debug("REST request to count UserQueries by criteria: {}", criteria);
        return ResponseEntity.ok().body(userQueryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-queries/:id} : get the "id" userQuery.
     *
     * @param id the id of the userQueryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userQueryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserQueryDTO> getUserQuery(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserQuery : {}", id);
        Optional<UserQueryDTO> userQueryDTO = userQueryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userQueryDTO);
    }

    /**
     * {@code DELETE  /user-queries/:id} : delete the "id" userQuery.
     *
     * @param id the id of the userQueryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserQuery(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserQuery : {}", id);
        userQueryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
