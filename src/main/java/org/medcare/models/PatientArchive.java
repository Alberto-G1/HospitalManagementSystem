package org.medcare.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_archives")
public class PatientArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int archiveId;

    private int originalPatientId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archived_by_user_id")
    private User archivedBy;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public PatientArchive() {}

    public PatientArchive(Patient patient, User archiver) {
        this.originalPatientId = patient.getPatientId();
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.phoneNumber = patient.getPhoneNumber();
        this.email = patient.getEmail();
        this.archivedBy = archiver;
        this.archivedAt = LocalDateTime.now();
    }

    // Getters and Setters...
    public int getArchiveId() { return archiveId; }
    public void setArchiveId(int archiveId) { this.archiveId = archiveId; }
    public int getOriginalPatientId() { return originalPatientId; }
    public void setOriginalPatientId(int originalPatientId) { this.originalPatientId = originalPatientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public User getArchivedBy() { return archivedBy; }
    public void setArchivedBy(User archivedBy) { this.archivedBy = archivedBy; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
}