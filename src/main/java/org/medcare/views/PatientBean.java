package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Gender;
import org.medcare.models.Patient;
import org.medcare.service.PatientService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PatientBean implements Serializable {

    @Inject
    private PatientService patientService;

    private List<Patient> patients;
    private Patient selectedPatient;

    @PostConstruct
    public void init() {
        patients = patientService.getAll();
    }

    public void openNew() {
        this.selectedPatient = new Patient();
    }

    public void savePatient() {
        if (this.selectedPatient.getPatientId() == 0) {
            patientService.save(this.selectedPatient);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Patient Registered");
        } else {
            patientService.save(this.selectedPatient);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Patient Updated");
        }
        // Refresh list from DB and update UI
        patients = patientService.getAll();
        // This JS is for PrimeFaces to update components after an AJAX request
        PrimeFaces.current().ajax().update("form:dt-patients");
    }

    public void deletePatient() {
        if (this.selectedPatient != null) {
            patientService.delete(this.selectedPatient);
            this.patients.remove(this.selectedPatient);
            this.selectedPatient = null;
            addMessage(FacesMessage.SEVERITY_WARN, "Removed", "Patient has been deleted.");
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No patient selected for deletion.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Gender[] getGenders() {
        return Gender.values();
    }

    // --- Standard Getters and Setters ---
    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
}