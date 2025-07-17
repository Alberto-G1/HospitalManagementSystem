package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.medcare.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    @NotNull(message = "Patient is required.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Doctor is required.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_doctorId", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Date is required.")
    @FutureOrPresent(message = "Date must be today or in the future.")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Time is required.")
    @Column(nullable = false)
    private LocalTime time;

    @Size(max = 255, message = "Reason must be less than 255 characters.")
    @Column
    private String reason;

    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    // --- Getters and Setters ---
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    @PrePersist
    protected void initializeDefaults() {
        super.onCreate();  // Auditable fields
        if (this.status == null) { // Only set default if it's not already set
            this.status = AppointmentStatus.SCHEDULED;
        }
    }
}