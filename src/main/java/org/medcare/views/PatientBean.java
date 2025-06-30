package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Gender;
import org.medcare.enums.Role;
import org.medcare.models.Patient;
import org.medcare.models.PatientArchive;
import org.medcare.models.User;
import org.medcare.service.PatientService;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PatientBean implements Serializable {

    @Inject private PatientService patientService;
    @Inject private UserBean userBean;

    private List<Patient> activePatients;
    private List<PatientArchive> archivedPatients;
    private Patient selectedPatient;

    @PostConstruct
    public void init() {
        activePatients = patientService.getAllActive();
        // Only Admin and Receptionist can see archived records
        if (userBean.getUser().getRole() == Role.ADMIN || userBean.getUser().getRole() == Role.RECEPTIONIST) {
            archivedPatients = patientService.getAllArchived();
        }
    }

    public void openNew() {
        selectedPatient = new Patient();
    }

    public void savePatient() {
        User currentUser = userBean.getUser();
        boolean isNew = (selectedPatient.getPatientId() == 0);

        try {
            if (isNew) {
                patientService.createPatient(selectedPatient, currentUser);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Patient Registered");
            } else {
                patientService.updatePatient(selectedPatient, currentUser);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Patient Updated");
            }
            init(); // Refresh lists
            PrimeFaces.current().executeScript("PF('managePatientDialog').hide()");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Operation failed.");
            e.printStackTrace();
        }
    }

    public void deletePatient() {
        if (selectedPatient != null) {
            patientService.archivePatient(selectedPatient.getPatientId(), userBean.getUser());
            init(); // Refresh lists
            addMessage(FacesMessage.SEVERITY_WARN, "Patient Archived", "The patient record has been moved to the archive.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Gender[] getGenders() {
        return Gender.values();
    }

    // --- Getters and Setters ---
    public List<Patient> getActivePatients() { return activePatients; }
    public void setActivePatients(List<Patient> activePatients) { this.activePatients = activePatients; }
    public List<PatientArchive> getArchivedPatients() { return archivedPatients; }
    public void setArchivedPatients(List<PatientArchive> archivedPatients) { this.archivedPatients = archivedPatients; }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
}