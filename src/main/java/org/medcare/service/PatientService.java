package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.PatientDAO;
import org.medcare.models.Patient;
import org.medcare.models.User;
import org.medcare.service.interfaces.PatientServiceInterface;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PatientService implements PatientServiceInterface {

    @Inject private PatientDAO patientDAO;
    @Inject private ActivityLogService activityLogService;

    @Override
    public void createPatient(Patient patient, User creator) {
        patient.setCreatedBy(creator);
        patientDAO.saveOrUpdate(patient);
        activityLogService.log("PATIENT_CREATED", "Registered new patient: " + patient.getFirstName() + " " + patient.getLastName(), creator);
    }

    @Override
    public void updatePatient(Patient patient, User updater) {
        patient.setLastUpdatedBy(updater);
        patientDAO.saveOrUpdate(patient);
        activityLogService.log("PATIENT_UPDATED", "Updated patient record: " + patient.getFirstName() + " " + patient.getLastName(), updater);
    }

    @Override
    public void softDeletePatient(Patient patient, User deleter) {
        if (patient != null) {
            patient.setActive(false);
            patient.setLastUpdatedBy(deleter);
            patientDAO.update(patient);
            activityLogService.log("PATIENT_DEACTIVATED", "Deactivated patient: " + patient.getFirstName(), deleter);
        }
    }

    @Override
    public void reactivatePatient(Patient patient, User reactivator) {
        if (patient != null) {
            patient.setActive(true);
            patient.setLastUpdatedBy(reactivator);
            patientDAO.update(patient);
            activityLogService.log("PATIENT_REACTIVATED", "Reactivated patient: " + patient.getFirstName(), reactivator);
        }
    }

    @Override
    public List<Patient> getAllActive() {
        return patientDAO.findAll().stream()
                .filter(Patient::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> getAllInactive() {
        return patientDAO.findAll().stream()
                .filter(p -> !p.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public Patient getById(int id) {
        return patientDAO.findById(id);
    }

    @Override
    public Patient getByIdWithRecords(int id) {
        return patientDAO.findByIdWithRecords(id);
    }

    @Override
    public long getActivePatientCount() {
        return patientDAO.countActive();
    }
}