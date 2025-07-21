package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Appointment;
import org.medcare.models.MedicalRecord;
import org.medcare.models.Patient;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.service.interfaces.PatientServiceInterface;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class PatientDetailBean implements Serializable {

    @Inject private PatientServiceInterface patientService;
    @Inject private AppointmentServiceInterface appointmentService;
    @Inject private UserBean userBean;

    private int patientId;
    private Patient patient;
    private List<MedicalRecord> medicalRecords;
    private List<Appointment> appointments;

    private boolean editMode = false;

    /**
     * This method is called by the <f:viewAction> tag in the XHTML.
     * It loads the patient and all related data based on the 'id' URL parameter.
     */
    public void loadPatient() {
        if (patientId > 0) {
            this.patient = patientService.getByIdWithRecords(patientId);
            if (this.patient != null) {
                // The patient object already contains the medical records due to eager fetching.
                this.medicalRecords = this.patient.getMedicalRecords().stream()
                        .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
                        .collect(Collectors.toList());

                // We need to fetch appointments separately.
                // In a large system, this would be a dedicated service call like findByPatientId().
                this.appointments = appointmentService.getAllActive().stream()
                        .filter(a -> a.getPatient().getPatientId() == patientId)
                        .sorted(Comparator.comparing(Appointment::getDate).reversed())
                        .collect(Collectors.toList());
            } else {
                // Handle case where patient ID is invalid
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Patient not found.");
            }
        }
    }

    /**
     * Saves changes to the patient's demographic information.
     * @return Navigation string to redirect back to the patient list.
     */
    public String savePatient() {
        try {
            patientService.updatePatient(patient, userBean.getUser());
            addFlashMessage(FacesMessage.SEVERITY_INFO, "Success", "Patient details updated successfully.");
            return "/app/patients.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Save Failed", e.getMessage());
            return null; // Stay on the page to show the error
        }
    }

    public void enableEditMode() { this.editMode = true; }
    public void cancelEdit() {
        this.editMode = false;
        loadPatient(); // Reload original data to discard changes
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    private void addFlashMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        addMessage(severity, summary, detail);
    }

    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public List<Appointment> getAppointments() { return appointments; }
    public boolean isEditMode() { return editMode; }
}