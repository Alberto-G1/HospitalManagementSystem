package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.PatientDAO;
import org.medcare.models.Patient;

import java.util.List;

@ApplicationScoped
public class PatientService {
    @Inject
    private PatientDAO patientDAO;

    public void save(Patient patient) {
        patientDAO.saveOrUpdate(patient);
    }

    public void delete(Patient patient) {
        patientDAO.delete(patient);
    }

    public List<Patient> getAll() {
        return patientDAO.findAll();
    }

    public Patient getById(int id) {
        return patientDAO.findById(id);
    }
}
