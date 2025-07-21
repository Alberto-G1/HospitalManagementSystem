package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.AppointmentStatus;
import org.medcare.models.Appointment;
import org.medcare.models.Doctor;
import org.medcare.models.Patient;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.service.interfaces.DoctorServiceInterface;
import org.medcare.service.interfaces.PatientServiceInterface;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Named
@ViewScoped
public class AppointmentActionBean implements Serializable {

    @Inject private AppointmentServiceInterface appointmentService;
    @Inject private PatientServiceInterface patientService;
    @Inject private DoctorServiceInterface doctorService;
    @Inject private UserBean userBean;

    private int appointmentId;
    private Appointment appointment;
    private boolean isNewAppointment;

    private List<Patient> availablePatients;
    private List<Doctor> availableDoctors;

    /**
     * This is the core method called by f:viewAction on all three new pages.
     * It inspects the 'id' URL parameter to decide what to do.
     */
    public void load() {
        if (appointmentId == 0) { // If id=0, it's a "New Appointment" action
            isNewAppointment = true;
            appointment = new Appointment();
            appointment.setDate(LocalDate.now());
            appointment.setTime(LocalTime.of(9, 0));
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        } else { // If id is anything else, it's an "Edit" or "View" action
            isNewAppointment = false;
            appointment = appointmentService.getById(appointmentId);
            if (appointment == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Appointment not found for ID: " + appointmentId);
            }
        }

        // These are needed for the dropdowns on the Add/Edit forms
        availablePatients = patientService.getAllActive();
        availableDoctors = doctorService.getAll();
    }

    /**
     * Saves the appointment (either creating or updating) and redirects to the list page.
     */
    public String save() {
        try {
            if (isNewAppointment) {
                appointmentService.createAppointment(appointment, userBean.getUser());
                addFlashMessage(FacesMessage.SEVERITY_INFO, "Success", "New appointment created successfully.");
            } else {
                appointmentService.updateAppointment(appointment, userBean.getUser());
                addFlashMessage(FacesMessage.SEVERITY_INFO, "Success", "Appointment updated successfully.");
            }
            return "/app/appointments.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Save Failed", e.getMessage());
            return null; // Stay on the current page to display the error
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    private void addFlashMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        addMessage(severity, summary, detail);
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public boolean isNewAppointment() { return isNewAppointment; }
    public List<Patient> getAvailablePatients() { return availablePatients; }
    public List<Doctor> getAvailableDoctors() { return availableDoctors; }
}