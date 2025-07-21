package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.medcare.dao.AppointmentArchiveDAO;
import org.medcare.dao.AppointmentDAO;
import org.medcare.dao.PatientDAO;
import org.medcare.enums.AppointmentStatus;
import org.medcare.models.Appointment;
import org.medcare.models.AppointmentArchive;
import org.medcare.models.Patient;
import org.medcare.models.User;
import org.medcare.service.interfaces.ActivityLogServiceInterface;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.utils.HibernateUtil;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class AppointmentService implements AppointmentServiceInterface {

    @Inject private AppointmentDAO appointmentDAO;
    @Inject private AppointmentArchiveDAO archiveDAO;
    @Inject private ActivityLogServiceInterface activityLogService;

    private void validateAppointment(Appointment appointment) {
        if (appointment == null) throw new ValidationException("Appointment object cannot be null.");
        if (appointment.getPatient() == null) throw new ValidationException("Patient is required for an appointment.");
        if (appointment.getDoctor() == null) throw new ValidationException("Doctor is required for an appointment.");
        if (appointment.getDate() == null) throw new ValidationException("Appointment date is required.");
        if (appointment.getTime() == null) throw new ValidationException("Appointment time is required.");
        if (appointment.getDate().isBefore(java.time.LocalDate.now())) {
            throw new ValidationException("Appointment date cannot be in the past.");
        }
    }

    @Override
    @Transactional
    public Appointment createAppointment(Appointment appointment, User creator) {
        validateAppointment(appointment);
        Objects.requireNonNull(creator, "A user must be specified as the creator.");

        appointment.setCreatedBy(creator);
        appointment.setLastUpdatedBy(creator);
        appointmentDAO.save(appointment);

        activityLogService.log("APPOINTMENT_CREATED", "Booked appointment for " + appointment.getPatient().getFirstName() + " with Dr. " + appointment.getDoctor().getLastName(), creator);
        return appointment;
    }

    @Override
    @Transactional
    public Appointment updateAppointment(Appointment appointment, User updater) {
        validateAppointment(appointment);
        Objects.requireNonNull(updater, "A user must be specified as the updater.");

        Appointment existing = appointmentDAO.findById(appointment.getAppointmentId());
        if(existing == null) {
            throw new ValidationException("Appointment not found for update.");
        }

        existing.setPatient(appointment.getPatient());
        existing.setDoctor(appointment.getDoctor());
        existing.setDate(appointment.getDate());
        existing.setTime(appointment.getTime());
        existing.setReason(appointment.getReason());
        existing.setStatus(appointment.getStatus());
        existing.setLastUpdatedBy(updater);

        appointmentDAO.update(existing);
        activityLogService.log("APPOINTMENT_UPDATED", "Updated appointment ID " + existing.getAppointmentId(), updater);
        return existing;
    }

    @Override
    @Transactional
    public void updateAppointmentStatus(int appointmentId, AppointmentStatus newStatus, User updater) {
        Objects.requireNonNull(updater, "Updater cannot be null.");
        if (newStatus == null) {
            throw new ValidationException("A new status must be provided.");
        }

        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            throw new ValidationException("Appointment with ID " + appointmentId + " not found.");
        }

        appointment.setStatus(newStatus);
        appointment.setLastUpdatedBy(updater);
        appointmentDAO.update(appointment);

        activityLogService.log("APPOINTMENT_STATUS_UPDATED", "Status for appointment ID " + appointment.getAppointmentId() + " changed to " + newStatus, updater);
    }

    @Override
    @Transactional
    public void archiveAppointment(int appointmentId, User archiver) {
        Objects.requireNonNull(archiver, "Archiver user cannot be null.");

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // 1. Fetch the appointment within our controlled session
            Appointment appointment = session.get(Appointment.class, appointmentId);

            if (appointment != null) {
                // 2. Access the patient and its collection. Because the session is open,
                // Hibernate can now lazily initialize the appointments collection.
                Patient patient = appointment.getPatient();
                if (patient != null) {
                    // This line will now succeed
                    patient.getAppointments().remove(appointment);
                    session.merge(patient); // Tell Hibernate to save the change to the patient's collection
                }

                // 3. Create the archive record
                AppointmentArchive archive = new AppointmentArchive(appointment, archiver);
                session.persist(archive); // Use session.persist for new objects in the transaction

                // 4. Log the activity
                String logDetails = String.format("Archived appointment ID %d for %s with Dr. %s",
                        appointment.getAppointmentId(),
                        (patient != null ? patient.getFirstName() + " " + patient.getLastName() : "N/A"),
                        (appointment.getDoctor() != null ? appointment.getDoctor().getLastName() : "N/A"));
                activityLogService.log("APPOINTMENT_ARCHIVED", logDetails, archiver);

                // 5. Remove the original appointment
                session.remove(appointment);

                // 6. Commit all changes at once
                transaction.commit();
            } else {
                throw new ValidationException("Appointment with ID " + appointmentId + " not found for archiving.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            // Re-throw as a runtime exception to inform the caller of the failure
            throw new RuntimeException("Could not delete appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appointment> getAllActive() {
        return appointmentDAO.findAll();
    }

    @Override
    public List<AppointmentArchive> getAllArchived() {
        return archiveDAO.findAll();
    }

    @Override
    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor ID must be positive.");
        }
        return appointmentDAO.findByDoctorId(doctorId);
    }

    @Override
    public Appointment getById(int id) {
        if (id <= 0) {
            return null;
        }
        return appointmentDAO.findById(id);
    }
}