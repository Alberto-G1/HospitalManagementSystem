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
import java.util.*;
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
    private List<MedicalRecord> filteredPatientRecords;
    private Patient selectedPatient;
    private List<MedicalRecord> selectedPatientRecords;
    private MedicalRecord newRecord;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idParam = params.get("patientId");

        if (idParam != null) {
            try {
                int patientId = Integer.parseInt(idParam);
                this.selectedPatient = patientService.getByIdWithRecords(patientId);
                if (this.selectedPatient != null && this.selectedPatient.getMedicalRecords() != null) {
                    this.selectedPatientRecords = this.selectedPatient.getMedicalRecords().stream()
                            .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
                            .collect(Collectors.toList());
                } else {
                    this.selectedPatientRecords = Collections.emptyList();
                }
                this.newRecord = new MedicalRecord();
                return;
            } catch (NumberFormatException ignored) {
                // fallback to default
            }
        }

        loadPatients();
        this.newRecord = new MedicalRecord();
    }

    private void loadPatients() {
        activePatients = patientService.getAllActive();
        inactivePatients = (userBean.isAdmin() || userBean.isReceptionist()) ?
                patientService.getAllInactive() : Collections.emptyList();
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

    public void viewMedicalRecords(Patient patient) {
        this.selectedPatient = patientService.getByIdWithRecords(patient.getPatientId());
        if (this.selectedPatient != null && this.selectedPatient.getMedicalRecords() != null) {
            this.selectedPatientRecords = this.selectedPatient.getMedicalRecords().stream()
                    .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
                    .collect(Collectors.toList());
        } else {
            this.selectedPatientRecords = Collections.emptyList();
        }
        this.newRecord = new MedicalRecord();
    }

    public void addMedicalRecord() {
        try {
            User currentUser = userBean.getUser();
            Doctor doctorProfile = doctorService.findByUserId(currentUser.getUserId());
            if (doctorProfile == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your doctor profile could not be found.");
                return;
            }
            medicalRecordService.addMedicalRecord(selectedPatient, doctorProfile, newRecord.getNotes(), newRecord.getPrescription(), currentUser);
            viewMedicalRecords(this.selectedPatient);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Medical record added.");
            PrimeFaces.current().ajax().update("recordForm:messages", "recordForm:recordsTable");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not add medical record.");
            e.printStackTrace();
        }
    }

    public int calculateAge(LocalDate dateOfBirth) {
        return (dateOfBirth == null) ? 0 : Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<Patient> getActivePatients() { return activePatients; }
    public List<Patient> getInactivePatients() { return inactivePatients; }
    public List<Patient> getFilteredPatients() { return filteredPatients; }
    public void setFilteredPatients(List<Patient> filteredPatients) { this.filteredPatients = filteredPatients; }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
    public List<MedicalRecord> getSelectedPatientRecords() { return selectedPatientRecords; }
    public void setSelectedPatientRecords(List<MedicalRecord> selectedPatientRecords) { this.selectedPatientRecords = selectedPatientRecords; }
    public MedicalRecord getNewRecord() { return newRecord; }
    public void setNewRecord(MedicalRecord newRecord) { this.newRecord = newRecord; }
    public Gender[] getGenders() { return Gender.values(); }
    public List<MedicalRecord> getFilteredPatientRecords() { return filteredPatientRecords; }
    public void setFilteredPatientRecords(List<MedicalRecord> filteredPatientRecords) { this.filteredPatientRecords = filteredPatientRecords; }
}