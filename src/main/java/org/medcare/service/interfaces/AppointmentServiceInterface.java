package org.medcare.service.interfaces;

import org.medcare.enums.AppointmentStatus;
import org.medcare.models.Appointment;
import org.medcare.models.AppointmentArchive;
import org.medcare.models.User;

import java.util.List;

public interface AppointmentServiceInterface {

    /**
     * Creates a new appointment in the system.
     */
    Appointment createAppointment(Appointment appointment, User creator);

    /**
     * Updates an existing appointment's details (patient, doctor, date, etc.).
     */
    Appointment updateAppointment(Appointment appointment, User updater);

    /**
     * Updates only the status of an existing appointment.
     */
    void updateAppointmentStatus(int appointmentId, AppointmentStatus newStatus, User updater);

    /**
     * Archives and then deletes an appointment from the active list.
     */
    void archiveAppointment(int appointmentId, User archiver);

    /**
     * Retrieves all active (non-archived) appointments.
     */
    List<Appointment> getAllActive();

    /**
     * Retrieves all archived appointments.
     */
    List<AppointmentArchive> getAllArchived();

    /**
     * Retrieves all active appointments for a specific doctor.
     */
    List<Appointment> getAppointmentsForDoctor(int doctorId);

    /**
     * Retrieves a single appointment by its ID.
     */
    Appointment getById(int id);
}