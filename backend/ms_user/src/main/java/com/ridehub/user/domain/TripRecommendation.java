package com.ridehub.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A TripRecommendation.
 */
@Entity
@Table(name = "trip_recommendation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "origin", length = 200, nullable = false)
    private String origin;

    @NotNull
    @Size(max = 200)
    @Column(name = "destination", length = 200, nullable = false)
    private String destination;

    @NotNull
    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @Column(name = "preferred_time")
    private LocalTime preferredTime;

    @Size(max = 100)
    @Column(name = "budget_range", length = 100)
    private String budgetRange;

    @Size(max = 50)
    @Column(name = "seat_preference", length = 50)
    private String seatPreference;

    @Size(max = 2000)
    @Column(name = "recommended_trips", length = 2000)
    private String recommendedTrips;

    @Column(name = "confidence_score", precision = 21, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "is_booked")
    private Boolean isBooked;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "deleted_by", length = 36)
    private UUID deletedBy;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "profile", "statistics", "chatSessions", "recommendations" }, allowSetters = true)
    private AppUser user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TripRecommendation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return this.origin;
    }

    public TripRecommendation origin(String origin) {
        this.setOrigin(origin);
        return this;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return this.destination;
    }

    public TripRecommendation destination(String destination) {
        this.setDestination(destination);
        return this;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getTravelDate() {
        return this.travelDate;
    }

    public TripRecommendation travelDate(LocalDate travelDate) {
        this.setTravelDate(travelDate);
        return this;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public LocalTime getPreferredTime() {
        return this.preferredTime;
    }

    public TripRecommendation preferredTime(LocalTime preferredTime) {
        this.setPreferredTime(preferredTime);
        return this;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getBudgetRange() {
        return this.budgetRange;
    }

    public TripRecommendation budgetRange(String budgetRange) {
        this.setBudgetRange(budgetRange);
        return this;
    }

    public void setBudgetRange(String budgetRange) {
        this.budgetRange = budgetRange;
    }

    public String getSeatPreference() {
        return this.seatPreference;
    }

    public TripRecommendation seatPreference(String seatPreference) {
        this.setSeatPreference(seatPreference);
        return this;
    }

    public void setSeatPreference(String seatPreference) {
        this.seatPreference = seatPreference;
    }

    public String getRecommendedTrips() {
        return this.recommendedTrips;
    }

    public TripRecommendation recommendedTrips(String recommendedTrips) {
        this.setRecommendedTrips(recommendedTrips);
        return this;
    }

    public void setRecommendedTrips(String recommendedTrips) {
        this.recommendedTrips = recommendedTrips;
    }

    public BigDecimal getConfidenceScore() {
        return this.confidenceScore;
    }

    public TripRecommendation confidenceScore(BigDecimal confidenceScore) {
        this.setConfidenceScore(confidenceScore);
        return this;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Boolean getIsBooked() {
        return this.isBooked;
    }

    public TripRecommendation isBooked(Boolean isBooked) {
        this.setIsBooked(isBooked);
        return this;
    }

    public void setIsBooked(Boolean isBooked) {
        this.isBooked = isBooked;
    }

    public Integer getFeedbackRating() {
        return this.feedbackRating;
    }

    public TripRecommendation feedbackRating(Integer feedbackRating) {
        this.setFeedbackRating(feedbackRating);
        return this;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public TripRecommendation createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public TripRecommendation updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public TripRecommendation isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public TripRecommendation deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public TripRecommendation deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public AppUser getUser() {
        return this.user;
    }

    public void setUser(AppUser appUser) {
        this.user = appUser;
    }

    public TripRecommendation user(AppUser appUser) {
        this.setUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TripRecommendation)) {
            return false;
        }
        return getId() != null && getId().equals(((TripRecommendation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripRecommendation{" +
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
            "}";
    }
}
