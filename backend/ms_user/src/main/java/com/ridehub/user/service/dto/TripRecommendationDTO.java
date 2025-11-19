package com.ridehub.user.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.user.domain.TripRecommendation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripRecommendationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String origin;

    @NotNull
    @Size(max = 200)
    private String destination;

    @NotNull
    private LocalDate travelDate;

    private LocalTime preferredTime;

    @Size(max = 100)
    private String budgetRange;

    @Size(max = 50)
    private String seatPreference;

    @Size(max = 2000)
    private String recommendedTrips;

    private BigDecimal confidenceScore;

    private Boolean isBooked;

    private Integer feedbackRating;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private AppUserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getBudgetRange() {
        return budgetRange;
    }

    public void setBudgetRange(String budgetRange) {
        this.budgetRange = budgetRange;
    }

    public String getSeatPreference() {
        return seatPreference;
    }

    public void setSeatPreference(String seatPreference) {
        this.seatPreference = seatPreference;
    }

    public String getRecommendedTrips() {
        return recommendedTrips;
    }

    public void setRecommendedTrips(String recommendedTrips) {
        this.recommendedTrips = recommendedTrips;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(Boolean isBooked) {
        this.isBooked = isBooked;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public AppUserDTO getUser() {
        return user;
    }

    public void setUser(AppUserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TripRecommendationDTO)) {
            return false;
        }

        TripRecommendationDTO tripRecommendationDTO = (TripRecommendationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tripRecommendationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripRecommendationDTO{" +
            "id=" + getId() +
            ", origin='" + getOrigin() + "'" +
            ", destination='" + getDestination() + "'" +
            ", travelDate='" + getTravelDate() + "'" +
            ", preferredTime='" + getPreferredTime() + "'" +
            ", budgetRange='" + getBudgetRange() + "'" +
            ", seatPreference='" + getSeatPreference() + "'" +
            ", recommendedTrips='" + getRecommendedTrips() + "'" +
            ", confidenceScore=" + getConfidenceScore() +
            ", isBooked='" + getIsBooked() + "'" +
            ", feedbackRating=" + getFeedbackRating() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
