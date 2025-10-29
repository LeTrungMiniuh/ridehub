package com.ridehub.route.repository;

import com.ridehub.route.domain.Vehicle;
import com.ridehub.route.domain.enumeration.VehicleType;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Vehicle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    /**
     * Update typeFactor for all vehicles of a specific type.
     *
     * @param type the vehicle type.
     * @param typeFactor the new type factor.
     * @return number of vehicles updated.
     */
    @Modifying
    @Query("UPDATE Vehicle v SET v.typeFactor = :typeFactor WHERE v.type = :type")
    int updateTypeFactorByType(@Param("type") VehicleType type, @Param("typeFactor") BigDecimal typeFactor);
}
