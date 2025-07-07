package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.AppointmentStatus;
import org.medcare.enums.Role;
import org.medcare.models.*;
import org.medcare.service.AppointmentService;
import org.medcare.service.DoctorService;
import org.medcare.service.PatientService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AppointmentBean implements Serializable {

    @Inject private AppointmentService appointmentService;
    @Inject private DoctorService doctorService;
    @Inject private PatientService patientService;
    @Inject private UserBean userBean;

    private List<Appointment> appointments;
    private List<AppointmentArchive> archivedAppointments;
    private List<Doctor> availableDoctors;
    private List<Patient> availablePatients;

    private Appointment selectedAppointment;

    @PostConstruct
    public void init() {
        User currentUser = userBean.getUser();
        if (currentUser.getRole() == Role.DOCTOR) {
            Doctor doctorProfile = doctorService.findByUserId(currentUser.getUserId());
            if (doctorProfile != null) {
                appointments = appointmentService.getAppointmentsForDoctor(doctorProfile.getDoctorId());
            } else {
                appointments = new ArrayList<>(); // Doctor has no profile, show empty list
            }
        } else { // Admin or Receptionist
            appointments = appointmentService.getAllActive();
        }

        // Admin can also see archived appointments
        if (currentUser.getRole() == Role.ADMIN) {
            archivedAppointments = appointmentService.getAllArchived();
        }

        availableDoctors = doctorService.getAll(); // Lists only active doctors
        availablePatients = patientService.getAllActive();
    }

    public void openNew() {
        selectedAppointment = new Appointment();
        // Set default date and time to avoid nulls
        selectedAppointment.setDate(LocalDate.now());
        selectedAppointment.setTime(LocalTime.of(9, 0));
    }

    public void saveAppointment() {
        try {
            appointmentService.createAppointment(selectedAppointment, userBean.getUser());
            init(); // Refresh all lists
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Appointment booked successfully.");
            PrimeFaces.current().executeScript("PF('manageAppointmentDialog').hide()");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not book appointment.");
        }
    }

    public void updateStatus() {
        try {
            appointmentService.updateAppointmentStatus(selectedAppointment, selectedAppointment.getStatus(), userBean.getUser());
            init(); // Refresh lists
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Appointment status updated.");
            PrimeFaces.current().executeScript("PF('statusDialog').hide()");
        } catch(Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update status.");
        }
    }

    public void deleteAppointment() {
        try {
            appointmentService.archiveAppointment(selectedAppointment.getAppointmentId(), userBean.getUser());
            init(); // Refresh lists
            addMessage(FacesMessage.SEVERITY_WARN, "Success", "Appointment has been deleted and archived.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete appointment.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public AppointmentStatus[] getAppointmentStatuses() {
        return AppointmentStatus.values();
    }

    // --- Getters and Setters ---
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
    public List<AppointmentArchive> getArchivedAppointments() { return archivedAppointments; }
    public void setArchivedAppointments(List<AppointmentArchive> archivedAppointments) { this.archivedAppointments = archivedAppointments; }
    public List<Doctor> getAvailableDoctors() { return availableDoctors; }
    public void setAvailableDoctors(List<Doctor> availableDoctors) { this.availableDoctors = availableDoctors; }
    public List<Patient> getAvailablePatients() { return availablePatients; }
    public void setAvailablePatients(List<Patient> availablePatients) { this.availablePatients = availablePatients; }
    public Appointment getSelectedAppointment() { return selectedAppointment; }
    public void setSelectedAppointment(Appointment selectedAppointment) { this.selectedAppointment = selectedAppointment; }
}