package org.example.service;

import org.example.dao.PatientDAO;
import org.example.models.Patient;

import java.util.List;

public class PatientService {
    private final PatientDAO patientDAO = new PatientDAO();

    public void registerPatient(Patient patient) {
        patientDAO.save(patient);
    }

    public void deletePatient(Patient patient) {
        patientDAO.delete(patient);
    }

    public Patient getPatient(int id) {
        return patientDAO.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }
}
