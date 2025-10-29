package com.ridehub.route.service.dto;

import com.ridehub.route.domain.enumeration.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for updating typeFactor for all vehicles of a specific type.
 */
public class VehicleTypeFactorUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotNull(message = "Type factor is required")
    @DecimalMin(value = "0.0", message = "Type factor must be positive")
    private BigDecimal typeFactor;

    public VehicleTypeFactorUpdateDTO() {}

    public VehicleTypeFactorUpdateDTO(VehicleType type, BigDecimal typeFactor) {
        this.type = type;
        this.typeFactor = typeFactor;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public BigDecimal getTypeFactor() {
        return typeFactor;
    }

    public void setTypeFactor(BigDecimal typeFactor) {
        this.typeFactor = typeFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VehicleTypeFactorUpdateDTO)) {
            return false;
        }
        return type != null && type.equals(((VehicleTypeFactorUpdateDTO) o).type);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VehicleTypeFactorUpdateDTO{" +
            "type='" + type + "'" +
            ", typeFactor=" + typeFactor +
            "}";
    }
}