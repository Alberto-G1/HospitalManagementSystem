package org.example.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.enums.Gender;
import org.example.models.Patient;
import org.example.service.PatientService;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class PatientBean implements Serializable {

    private Patient patient = new Patient();
    private Patient selectedPatient;
    private String dateOfBirthStr;
    private String searchTerm = "";
    private List<Patient> patients;
    private List<Patient> filteredPatients;
    private boolean editMode = false;

    @Inject
    private PatientService patientService;

    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {
        loadPatients();
    }

    public void loadPatients() {
        patients = patientService.getAllPatients();
        filteredPatients = patients;
    }

    public String register() {
        try {
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                patient.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
            }

            if (patientService.registerPatient(patient, userBean.getUser().getUsername())) {
                resetForm();
                loadPatients();
                return "patients.xhtml?faces-redirect=true";
            }
            return null; // Stay on same page if validation fails

        } catch (DateTimeParseException e) {
//            patientService.addErrorMessage("Invalid Date", "Please enter date in YYYY-MM-DD format.");
            return null;
        }
    }

    public String update() {
        try {
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                patient.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
            }

            if (patientService.updatePatient(patient, userBean.getUser().getUsername())) {
                resetForm();
                loadPatients();
                editMode = false;
                return "patients.xhtml?faces-redirect=true";
            }
            return null;

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, Please enter date in YYYY-MM-DD format.");
//            patientService.addErrorMessage("Invalid Date", "Please enter date in YYYY-MM-DD format.");
            return null;
        }
    }

    public void edit(Patient p) {
        this.patient = new Patient();
        // Copy properties to avoid direct reference issues
//        this.patient.setPatientID(p.getPatientID());
        this.patient.setFirstName(p.getFirstName());
        this.patient.setLastName(p.getLastName());
        this.patient.setDateOfBirth(p.getDateOfBirth());
        this.patient.setGender(p.getGender());
        this.patient.setAddress(p.getAddress());
        this.patient.setPhoneNumber(p.getPhoneNumber());
        this.patient.setEmail(p.getEmail());
        this.patient.setEmergencyContact(p.getEmergencyContact());

        this.dateOfBirthStr = p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "";
        this.editMode = true;
    }

    public String delete(int id) {
        Patient p = patientService.getPatient(id);
        if (p != null && patientService.deletePatient(p, userBean.getUser().getUsername())) {
            loadPatients();
        }
        return "patients.xhtml?faces-redirect=true";
    }

    public void search() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            filteredPatients = patients;
        } else {
            filteredPatients = patientService.searchPatients(searchTerm.trim());
        }
    }

    public void clearSearch() {
        searchTerm = "";
        filteredPatients = patients;
    }

    public void cancelEdit() {
        resetForm();
        editMode = false;
    }

    private void resetForm() {
        patient = new Patient();
        dateOfBirthStr = "";
        selectedPatient = null;
    }

    public void viewDetails(Patient p) {
        selectedPatient = p;
    }

    // Utility methods for UI
    public List<Gender> getGenderOptions() {
        return Arrays.asList(Gender.values());
    }

    public String getPatientAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return "N/A";
        return String.valueOf(LocalDate.now().getYear() - dateOfBirth.getYear());
    }

    // Getters and Setters
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }

    public List<Patient> getPatients() { return patients; }
    public List<Patient> getFilteredPatients() { return filteredPatients; }

    public String getDateOfBirthStr() { return dateOfBirthStr; }
    public void setDateOfBirthStr(String dateOfBirthStr) { this.dateOfBirthStr = dateOfBirthStr; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
}