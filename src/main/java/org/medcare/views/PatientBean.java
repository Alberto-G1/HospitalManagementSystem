package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Gender;
import org.medcare.models.*;
import org.medcare.service.DoctorService;
import org.medcare.service.MedicalRecordService;
import org.medcare.service.PatientService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class PatientBean implements Serializable {

    @Inject private PatientService patientService;
    @Inject private UserBean userBean;
    @Inject private MedicalRecordService medicalRecordService;
    @Inject private DoctorService doctorService;

    private List<Patient> activePatients;
    private List<Patient> inactivePatients;
    private List<Patient> filteredPatients;
    private Patient selectedPatient;

    private List<MedicalRecord> selectedPatientRecords;
    private MedicalRecord newRecord;

    @PostConstruct
    public void init() {
        loadPatients();
        newRecord = new MedicalRecord();
    }

    private void loadPatients() {
        activePatients = patientService.getAllActive();
        if (userBean.isAdmin() || userBean.isReceptionist()) {
            inactivePatients = patientService.getAllInactive();
        } else {
            inactivePatients = Collections.emptyList();
        }
    }

    public void openNew() {
        selectedPatient = new Patient();
        selectedPatient.setGender(Gender.MALLE);
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
            loadPatients();
            PrimeFaces.current().executeScript("PF('managePatientDialog').hide()");
            PrimeFaces.current().ajax().update("patientForm:messages", "patientForm:patientTabs");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletePatient() {
        if (selectedPatient != null) {
            patientService.softDeletePatient(selectedPatient, userBean.getUser());
            loadPatients();
            addMessage(FacesMessage.SEVERITY_WARN, "Patient Deactivated", "The patient record has been moved to the inactive list.");
        }
    }

    public void reactivatePatient() {
        if (selectedPatient != null) {
            patientService.reactivatePatient(selectedPatient, userBean.getUser());
            loadPatients();
            addMessage(FacesMessage.SEVERITY_INFO, "Patient Reactivated", "The patient record is now active.");
        }
    }

    /**
     * Loads and prepares the medical records for the selected patient to be viewed in a dialog.
     * This method now calls a service method that EAGERLY fetches the records to prevent LazyInitializationException.
     * @param patient The patient whose records are to be viewed.
     */
    public void viewMedicalRecords(Patient patient) {
        // Use the new service method that fetches the patient AND their records together
        this.selectedPatient = patientService.getByIdWithRecords(patient.getPatientId());

        if (this.selectedPatient != null && this.selectedPatient.getMedicalRecords() != null) {
            // This access is now safe and will not throw an exception
            this.selectedPatientRecords = this.selectedPatient.getMedicalRecords().stream()
                    .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
                    .collect(Collectors.toList());
        } else {
            this.selectedPatientRecords = Collections.emptyList();
        }
        this.newRecord = new MedicalRecord(); // Reset for a new entry
    }

    /**
     * Adds a new medical record for the currently selected patient.
     */
    public void addMedicalRecord() {
        try {
            User currentUser = userBean.getUser();
            Doctor doctorProfile = doctorService.findByUserId(currentUser.getUserId());
            if (doctorProfile == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your doctor profile could not be found.");
                return;
            }
            medicalRecordService.addMedicalRecord(selectedPatient, doctorProfile, newRecord.getNotes(), newRecord.getPrescription(), currentUser);

            // After adding, refresh the view by re-fetching the patient with all their records
            viewMedicalRecords(this.selectedPatient);

            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Medical record added.");
            PrimeFaces.current().ajax().update("patientForm:medicalRecordsPanel");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not add medical record.");
            e.printStackTrace();
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (filterText == null || filterText.isEmpty()) {
            return true;
        }
        Patient patient = (Patient) value;
        return (patient.getFirstName() != null && patient.getFirstName().toLowerCase().contains(filterText)) ||
                (patient.getLastName() != null && patient.getLastName().toLowerCase().contains(filterText)) ||
                (patient.getPhoneNumber() != null && patient.getPhoneNumber().toLowerCase().contains(filterText)) ||
                (patient.getEmail() != null && patient.getEmail().toLowerCase().contains(filterText));
    }

    public int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters and Setters ---

    public List<Patient> getActivePatients() { return activePatients; }
    public void setActivePatients(List<Patient> activePatients) { this.activePatients = activePatients; }

    public List<Patient> getInactivePatients() { return inactivePatients; }
    public void setInactivePatients(List<Patient> inactivePatients) { this.inactivePatients = inactivePatients; }

    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }

    public List<MedicalRecord> getSelectedPatientRecords() { return selectedPatientRecords; }
    public void setSelectedPatientRecords(List<MedicalRecord> selectedPatientRecords) { this.selectedPatientRecords = selectedPatientRecords; }

    public MedicalRecord getNewRecord() { return newRecord; }
    public void setNewRecord(MedicalRecord newRecord) { this.newRecord = newRecord; }

    public Gender[] getGenders() { return Gender.values(); }

    public List<Patient> getFilteredPatients() { return filteredPatients; }
    public void setFilteredPatients(List<Patient> filteredPatients) { this.filteredPatients = filteredPatients; }
}