package com.ridehub.user.repository;

import com.ridehub.user.domain.UserQuery;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserQuery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserQueryRepository extends JpaRepository<UserQuery, Long>, JpaSpecificationExecutor<UserQuery> {}
