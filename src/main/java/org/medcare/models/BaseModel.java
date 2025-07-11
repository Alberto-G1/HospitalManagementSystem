package org.medcare.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseModel implements Serializable {

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "last_updated_by_user_id")
    private User lastUpdatedBy;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    // This 'active' flag is our soft-delete mechanism.
    @Column(nullable = false)
    private boolean active = true;

    // This will map to the problematic 'archived' column in the DB.
    // We will manage it automatically based on the 'active' status.
    @Column(nullable = false)
    private boolean archived = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        active = true;
        archived = false; // Ensure new records are not archived
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
        // Keep 'archived' in sync with 'active'
        this.archived = !this.active;
    }

    // Getters and Setters
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(User lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
        // Automatically update 'archived' when 'active' changes.
        this.archived = !active;
    }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) {
        this.archived = archived;
        // Keep 'active' in sync
        this.active = !archived;
    }
}