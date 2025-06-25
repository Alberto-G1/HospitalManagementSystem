package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.dao.AppointmentDAO;
import org.example.enums.AppointmentStatus;
import org.example.models.Appointment;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Named
@ApplicationScoped
public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Inject
    private ActivityLogService activityLogService;

    public boolean bookAppointment(Appointment appointment, String currentUser) {
        try {
            if (!validateAppointment(appointment)) {
                return false;
            }

            // Check for scheduling conflicts
            if (hasSchedulingConflict(appointment)) {
                addErrorMessage("Booking Failed", "Doctor is not available at the selected time. Please choose a different time slot.");
                return false;
            }

            // Check if appointment is in the past
            if (isAppointmentInPast(appointment)) {
                addErrorMessage("Booking Failed", "Cannot schedule appointments in the past.");
                return false;
            }

            // Set default status
            appointment.setStatus(AppointmentStatus.SCHEDULED);

            appointmentDAO.save(appointment);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "APPOINTMENT_BOOKED",
                        "Booked appointment for " + appointment.getPatient().getFirstName() + " " +
                                appointment.getPatient().getLastName() + " with Dr. " +
                                appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
            }

            addSuccessMessage("Success", "Appointment booked successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Booking Failed", "Error occurred while booking appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAppointment(Appointment appointment, String currentUser) {
        try {
            if (!validateAppointment(appointment)) {
                return false;
            }

            // Check for scheduling conflicts (excluding current appointment)
            if (hasSchedulingConflict(appointment, appointment.getAppointmentID())) {
                addErrorMessage("Update Failed", "Doctor is not available at the selected time. Please choose a different time slot.");
                return false;
            }

            // Check if appointment is in the past (only for future appointments)
            if (appointment.getStatus() == AppointmentStatus.SCHEDULED && isAppointmentInPast(appointment)) {
                addErrorMessage("Update Failed", "Cannot reschedule appointments to past dates.");
                return false;
            }

            appointmentDAO.save(appointment);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "APPOINTMENT_UPDATED",
                        "Updated appointment ID: " + appointment.getAppointmentID());
            }

            addSuccessMessage("Success", "Appointment updated successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Update Failed", "Error occurred while updating appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelAppointment(Appointment appointment, String currentUser) {
        try {
            if (appointment == null) {
                addErrorMessage("Cancel Failed", "Appointment not found.");
                return false;
            }

            // Check if appointment can be cancelled
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                addErrorMessage("Cancel Failed", "Cannot cancel completed appointments.");
                return false;
            }

            if (appointment.getStatus() == AppointmentStatus.CANCELED) {
                addErrorMessage("Cancel Failed", "Appointment is already cancelled.");
                return false;
            }

            appointment.setStatus(AppointmentStatus.CANCELED);
            appointmentDAO.save(appointment);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "APPOINTMENT_CANCELLED",
                        "Cancelled appointment ID: " + appointment.getAppointmentID());
            }

            addSuccessMessage("Success", "Appointment cancelled successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Cancel Failed", "Error occurred while cancelling appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean completeAppointment(Appointment appointment, String currentUser) {
        try {
            if (appointment == null) {
                addErrorMessage("Complete Failed", "Appointment not found.");
                return false;
            }

            if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
                addErrorMessage("Complete Failed", "Only scheduled appointments can be marked as completed.");
                return false;
            }

            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentDAO.save(appointment);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "APPOINTMENT_COMPLETED",
                        "Completed appointment ID: " + appointment.getAppointmentID());
            }

            addSuccessMessage("Success", "Appointment marked as completed!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Complete Failed", "Error occurred while completing appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAppointment(Appointment appointment, String currentUser) {
        try {
            if (appointment == null) {
                addErrorMessage("Delete Failed", "Appointment not found.");
                return false;
            }

            appointmentDAO.delete(appointment);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "APPOINTMENT_DELETED",
                        "Deleted appointment ID: " + appointment.getAppointmentID());
            }

            addSuccessMessage("Success", "Appointment deleted successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Delete Failed", "Error occurred while deleting appointment: " + e.getMessage());
            return false;
        }
    }

    public Appointment getAppointment(int id) {
        try {
            return appointmentDAO.findById(id);
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving appointment: " + e.getMessage());
            return null;
        }
    }

    public List<Appointment> getAllAppointments() {
        try {
            return appointmentDAO.findAll();
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving appointments: " + e.getMessage());
            return List.of();
        }
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        try {
            return appointmentDAO.findByDoctor(doctorId);
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving doctor appointments: " + e.getMessage());
            return List.of();
        }
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        try {
            return appointmentDAO.findByPatient(patientId);
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving patient appointments: " + e.getMessage());
            return List.of();
        }
    }

    public List<Appointment> getTodaysAppointments() {
        try {
            return appointmentDAO.findByDate(Date.valueOf(LocalDate.now()));
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving today's appointments: " + e.getMessage());
            return List.of();
        }
    }

    private boolean validateAppointment(Appointment appointment) {
        if (appointment == null) {
            addErrorMessage("Validation Error", "Appointment data is required.");
            return false;
        }

        // Validate patient
        if (appointment.getPatient() == null) {
            addErrorMessage("Validation Error", "Patient is required.");
            return false;
        }

        // Validate doctor
        if (appointment.getDoctor() == null) {
            addErrorMessage("Validation Error", "Doctor is required.");
            return false;
        }

        // Validate appointment date
        if (appointment.getAppointmentDate() == null) {
            addErrorMessage("Validation Error", "Appointment date is required.");
            return false;
        }

        // Validate appointment time
        if (appointment.getAppointmentTime() == null) {
            addErrorMessage("Validation Error", "Appointment time is required.");
            return false;
        }

        // Validate business hours (9 AM to 5 PM)
        LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();
        if (appointmentTime.isBefore(LocalTime.of(9, 0)) || appointmentTime.isAfter(LocalTime.of(17, 0))) {
            addErrorMessage("Validation Error", "Appointments can only be scheduled between 9:00 AM and 5:00 PM.");
            return false;
        }

        // Validate reason
        if (appointment.getReason() != null && appointment.getReason().length() > 255) {
            addErrorMessage("Validation Error", "Reason must be less than 255 characters.");
            return false;
        }

        return true;
    }

    private boolean hasSchedulingConflict(Appointment appointment) {
        return hasSchedulingConflict(appointment, 0);
    }

    private boolean hasSchedulingConflict(Appointment appointment, int excludeAppointmentId) {
        List<Appointment> existingAppointments = appointmentDAO.findByDoctorAndDate(
                appointment.getDoctor().getDoctorID(),
                appointment.getAppointmentDate()
        );

        return existingAppointments.stream()
                .anyMatch(existing -> existing.getAppointmentID() != excludeAppointmentId &&
                        existing.getStatus() == AppointmentStatus.SCHEDULED &&
                        existing.getAppointmentTime().equals(appointment.getAppointmentTime()));
    }

    private boolean isAppointmentInPast(Appointment appointment) {
        LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
        LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return appointmentDate.isBefore(today) ||
                (appointmentDate.equals(today) && appointmentTime.isBefore(now));
    }

    // Make addErrorMessage and addSuccessMessage public
    public void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    public void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

}


