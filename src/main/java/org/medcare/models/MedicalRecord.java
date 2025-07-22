package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
public class MedicalRecord extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotBlank(message = "Visit notes are required.")
    @Size(max = 1000, message = "Notes must be less than 1000 characters.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes;

    @Size(max = 1000, message = "Prescription must be less than 1000 characters.")
    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(nullable = false)
    private LocalDate visitDate;

    @PrePersist
    protected void onCreateRecord() {
        super.onCreate();
        visitDate = LocalDate.now();
    }

    // Getters and Setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
}
