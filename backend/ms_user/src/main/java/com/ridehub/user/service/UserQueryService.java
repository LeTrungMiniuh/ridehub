package com.ridehub.user.service;

import com.ridehub.user.service.dto.UserQueryDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.user.domain.UserQuery}.
 */
public interface UserQueryService {
    /**
     * Save a userQuery.
     *
     * @param userQueryDTO the entity to save.
     * @return the persisted entity.
     */
    UserQueryDTO save(UserQueryDTO userQueryDTO);

    /**
     * Updates a userQuery.
     *
     * @param userQueryDTO the entity to update.
     * @return the persisted entity.
     */
    UserQueryDTO update(UserQueryDTO userQueryDTO);

    /**
     * Partially updates a userQuery.
     *
     * @param userQueryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserQueryDTO> partialUpdate(UserQueryDTO userQueryDTO);

    /**
     * Get the "id" userQuery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserQueryDTO> findOne(Long id);

    /**
     * Delete the "id" userQuery.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
