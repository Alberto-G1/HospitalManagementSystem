package org.example.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.models.Patient;
import org.example.service.PatientService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Named
@RequestScoped
public class PatientBean {

    private Patient patient = new Patient();
    private String dateOfBirthStr; // To capture input as string
    private List<Patient> patients;

    @Inject
    private PatientService patientService;

    @PostConstruct
    public void init() {
        patients = patientService.getAllPatients();
    }

    public String register() {
        try {
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                patient.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
            }
            patientService.registerPatient(patient);
            this.patient = new Patient();
            this.dateOfBirthStr = "";
            return "patients.xhtml?faces-redirect=true";
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // You can add FacesMessage for feedback
            return null; // Stay on same page
        }
    }

    public String delete(int id) {
        Patient p = patientService.getPatient(id);
        if (p != null) {
            patientService.deletePatient(p);
        }
        return "patients.xhtml?faces-redirect=true";
    }

    // Getters & Setters
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public List<Patient> getPatients() { return patients; }

    public String getDateOfBirthStr() { return dateOfBirthStr; }
    public void setDateOfBirthStr(String dateOfBirthStr) { this.dateOfBirthStr = dateOfBirthStr; }
}
