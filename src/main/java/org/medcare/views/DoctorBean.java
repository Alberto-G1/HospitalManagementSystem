package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Doctor;
import org.medcare.service.DoctorService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DoctorBean implements Serializable {

    @Inject
    private DoctorService doctorService;

    private List<Doctor> doctors;
    private Doctor selectedDoctor;

    @PostConstruct
    public void init() {
        doctors = doctorService.getAll();
    }

    public void openNew() {
        this.selectedDoctor = new Doctor();
    }

    public void saveDoctor() {
        try {
            if (this.selectedDoctor.getDoctorId() == 0) {
                doctorService.save(this.selectedDoctor);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Doctor Added");
            } else {
                doctorService.update(this.selectedDoctor);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Doctor Updated");
            }
            // Refresh the list to ensure consistency
            doctors = doctorService.getAll();
            PrimeFaces.current().ajax().update("form:dt-doctors");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save doctor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteDoctor() {
        if (this.selectedDoctor != null) {
            try {
                doctorService.softDelete(this.selectedDoctor);
                // Refresh the list instead of manually removing
                doctors = doctorService.getAll();
                this.selectedDoctor = null;
                addMessage(FacesMessage.SEVERITY_WARN, "Deactivated", "Doctor has been deactivated.");
                PrimeFaces.current().ajax().update("form:dt-doctors");
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not deactivate doctor: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No doctor selected for deletion.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Standard Getters and Setters ---
    public List<Doctor> getDoctors() { return doctors; }
    public void setDoctors(List<Doctor> doctors) { this.doctors = doctors; }
    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(Doctor selectedDoctor) { this.selectedDoctor = selectedDoctor; }
}
