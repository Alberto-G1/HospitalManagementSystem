package org.medcare.models;

import jakarta.persistence.*;
import org.medcare.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment_archives")
public class AppointmentArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int archiveId;

    // Fields copied from the original appointment
    private int originalAppointmentId;
    private int patientId;
    private String patientName;
    private int doctorId;
    private String doctorName;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private AppointmentStatus status;

    // Auditing fields for the archive record itself
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archived_by_user_id")
    private User archivedBy;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    // Default constructor
    public AppointmentArchive() {}

    // Constructor to easily create an archive from an appointment
    public AppointmentArchive(Appointment appointment, User archiver) {
        this.originalAppointmentId = appointment.getAppointmentId();
        this.patientId = appointment.getPatient().getPatientId();
        this.patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        this.doctorId = appointment.getDoctor().getDoctorId();
        this.doctorName = appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.reason = appointment.getReason();
        this.status = appointment.getStatus();
        this.archivedBy = archiver;
        this.archivedAt = LocalDateTime.now();
    }

    // Getters and Setters...
    public int getArchiveId() { return archiveId; }
    public void setArchiveId(int archiveId) { this.archiveId = archiveId; }
    public int getOriginalAppointmentId() { return originalAppointmentId; }
    public void setOriginalAppointmentId(int originalAppointmentId) { this.originalAppointmentId = originalAppointmentId; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public User getArchivedBy() { return archivedBy; }
    public void setArchivedBy(User archivedBy) { this.archivedBy = archivedBy; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
}