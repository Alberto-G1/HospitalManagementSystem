package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Appointment;
import org.medcare.models.Doctor;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.service.interfaces.DoctorServiceInterface;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class DoctorDetailBean implements Serializable {

    @Inject private DoctorServiceInterface doctorService;
    @Inject private AppointmentServiceInterface appointmentService;
    @Inject private UserBean userBean;

    private int doctorId;
    private Doctor doctor;
    private List<Appointment> appointments;

    private boolean editMode = false;

    public void loadDoctor() {
        if (doctorId > 0) {
            this.doctor = doctorService.getByIdIncludeInactive(doctorId); // Admins can see inactive too
            if (this.doctor != null) {
                this.appointments = appointmentService.getAppointmentsForDoctor(doctorId).stream()
                        .sorted(Comparator.comparing(Appointment::getDate).reversed())
                        .collect(Collectors.toList());
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Doctor not found.");
            }
        }
    }

    public String saveDoctor() {
        try {
            doctorService.saveOrUpdate(doctor, userBean.getUser());
            addFlashMessage(FacesMessage.SEVERITY_INFO, "Success", "Doctor profile updated successfully.");
            return "/app/staff.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Save Failed", e.getMessage());
            return null;
        }
    }

    public void enableEditMode() { this.editMode = true; }
    public void cancelEdit() {
        this.editMode = false;
        loadDoctor();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    private void addFlashMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        addMessage(severity, summary, detail);
    }

    // Getters and Setters
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public List<Appointment> getAppointments() { return appointments; }
    public boolean isEditMode() { return editMode; }
}