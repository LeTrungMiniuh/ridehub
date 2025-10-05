package com.ridehub.user.web.rest;

import com.ridehub.user.repository.AppUserRepository;
import com.ridehub.user.service.AppUserQueryService;
import com.ridehub.user.service.AppUserService;
import com.ridehub.user.service.KeycloakAuthService;
import com.ridehub.user.service.criteria.AppUserCriteria;
import com.ridehub.user.service.dto.AppUserDTO;
import com.ridehub.user.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.user.domain.AppUser}.
 */
@RestController
@RequestMapping("/api/app-users")
public class AppUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserResource.class);

    private static final String ENTITY_NAME = "msUserAppUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppUserService appUserService;

    private final AppUserRepository appUserRepository;

    private final AppUserQueryService appUserQueryService;

    private final KeycloakAuthService keycloakAuthService;

    public AppUserResource(AppUserService appUserService, AppUserRepository appUserRepository, AppUserQueryService appUserQueryService, KeycloakAuthService keycloakAuthService) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.appUserQueryService = appUserQueryService;
        this.keycloakAuthService = keycloakAuthService;
    }

    /**
     * {@code POST  /app-users} : Create a new appUser.
     *
     * @param appUserDTO the appUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appUserDTO, or with status {@code 400 (Bad Request)} if the appUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AppUserDTO> createAppUser(@Valid @RequestBody AppUserDTO appUserDTO) throws URISyntaxException {
        LOG.debug("REST request to save AppUser : {}", appUserDTO);
        if (appUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new appUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        appUserDTO = appUserService.save(appUserDTO);
        return ResponseEntity.created(new URI("/api/app-users/" + appUserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, appUserDTO.getId().toString()))
            .body(appUserDTO);
    }

    /**
     * {@code PUT  /app-users/:id} : Updates an existing appUser.
     *
     * @param id the id of the appUserDTO to save.
     * @param appUserDTO the appUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserDTO,
     * or with status {@code 400 (Bad Request)} if the appUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppUserDTO> updateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AppUserDTO appUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppUser : {}, {}", id, appUserDTO);
        if (appUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        appUserDTO = appUserService.update(appUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUserDTO.getId().toString()))
            .body(appUserDTO);
    }

    /**
     * {@code PATCH  /app-users/:id} : Partial updates given fields of an existing appUser, field will ignore if it is null
     *
     * @param id the id of the appUserDTO to save.
     * @param appUserDTO the appUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserDTO,
     * or with status {@code 400 (Bad Request)} if the appUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AppUserDTO> partialUpdateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AppUserDTO appUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppUser partially : {}, {}", id, appUserDTO);
        if (appUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppUserDTO> result = appUserService.partialUpdate(appUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /app-users} : get all the appUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appUsers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers(AppUserCriteria criteria) {
        LOG.debug("REST request to get AppUsers by criteria: {}", criteria);

        List<AppUserDTO> entityList = appUserQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /app-users/count} : count all the appUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAppUsers(AppUserCriteria criteria) {
        LOG.debug("REST request to count AppUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(appUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /app-users/:id} : get the "id" appUser.
     *
     * @param id the id of the appUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDTO> getAppUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AppUser : {}", id);
        Optional<AppUserDTO> appUserDTO = appUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appUserDTO);
    }

    /**
     * {@code DELETE  /app-users/:id} : delete the "id" appUser.
     *
     * @param id the id of the appUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AppUser : {}", id);
        appUserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code PUT  /app-users/:id/disable} : disable the "id" appUser.
     * Sets delete status and disables the user in Keycloak.
     *
     * @param id the id of the appUserDTO to disable.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the disabled appUserDTO,
     * or with status {@code 404 (Not Found)} if the appUserDTO is not found.
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Map<String, Object>> disableAppUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to disable AppUser : {}", id);

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        // Get current user from security context to track who is disabling the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID deletedBy = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            if (sub != null) {
                try {
                    deletedBy = UUID.fromString(sub);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Invalid UUID format for user ID: {}", sub);
                }
            }
        }

        // Find the user to get their Keycloak ID
        Optional<AppUserDTO> userOptional = appUserService.findOne(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUserDTO user = userOptional.orElseThrow();

        // Disable user in Keycloak first
        Map<String, Object> keycloakResult = keycloakAuthService.adminDisableUser(user.getKeycloakId().toString());

        if (!"success".equals(keycloakResult.get("status"))) {
            LOG.error("Failed to disable user in Keycloak: {}", keycloakResult.get("message"));
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Failed to disable user in Keycloak: " + keycloakResult.get("message")
            ));
        }

        // Disable user locally
        AppUserDTO result = appUserService.disableUser(id, deletedBy)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LOG.info("Successfully disabled user with id: {} by admin: {}", id, deletedBy);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(Map.of(
                "status", "success",
                "message", "User disabled successfully",
                "user", result
            ));
    }
}
