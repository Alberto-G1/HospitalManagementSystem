package org.example.controller;

import org.example.models.Patient;
import org.example.service.PatientService;

import java.util.List;

public class PatientController {
    private final PatientService patientService = new PatientService();

    public void registerPatient(Patient patient) {
        patientService.registerPatient(patient);
    }

    public void deletePatient(int id) {
        Patient p = patientService.getPatient(id);
        if (p != null) {
            patientService.deletePatient(p);
            System.out.println("✅ Patient deleted.");
        } else {
            System.out.println("❌ Patient not found.");
        }
    }

    public List<Patient> listPatients() {
        return patientService.getAllPatients();
    }
}
