package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.PatientDAO;
import org.medcare.models.Patient;
import org.medcare.models.User;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PatientService {

    @Inject private PatientDAO patientDAO;
    @Inject private ActivityLogService activityLogService;

    public void createPatient(Patient patient, User creator) {
        patient.setCreatedBy(creator);
        // The 'active' and 'archived' fields are handled by @PrePersist in BaseModel
        patientDAO.saveOrUpdate(patient);
        activityLogService.log("PATIENT_CREATED", "Registered new patient: " + patient.getFirstName() + " " + patient.getLastName(), creator);
    }

    public void updatePatient(Patient patient, User updater) {
        patient.setLastUpdatedBy(updater);
        patientDAO.saveOrUpdate(patient);
        activityLogService.log("PATIENT_UPDATED", "Updated patient record: " + patient.getFirstName() + " " + patient.getLastName(), updater);
    }

    public void softDeletePatient(Patient patient, User deleter) {
        if (patient != null) {
            patient.setActive(false); // This will also set archived=true
            patient.setLastUpdatedBy(deleter);
            patientDAO.update(patient);
            activityLogService.log("PATIENT_DEACTIVATED", "Deactivated patient: " + patient.getFirstName(), deleter);
        }
    }

    public void reactivatePatient(Patient patient, User reactivator) {
        if (patient != null) {
            patient.setActive(true); // This will also set archived=false
            patient.setLastUpdatedBy(reactivator);
            patientDAO.update(patient);
            activityLogService.log("PATIENT_REACTIVATED", "Reactivated patient: " + patient.getFirstName(), reactivator);
        }
    }

    /**
     * Gets a list of all patients. The DAO is now configured to fetch medical records
     * along with them to prevent lazy loading issues in some scenarios.
     * We still filter for active status here in the service layer.
     * @return A list of active patients.
     */
    public List<Patient> getAllActive() {
        return patientDAO.findAll().stream()
                .filter(Patient::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all inactive (archived) patients.
     * @return A list of inactive patients.
     */
    public List<Patient> getAllInactive() {
        return patientDAO.findAll().stream()
                .filter(p -> !p.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Finds a patient by their ID without necessarily loading related collections.
     * @param id The patient's ID.
     * @return The Patient object.
     */
    public Patient getById(int id) {
        return patientDAO.findById(id);
    }

    /**
     * Finds a patient by their ID and guarantees that the medicalRecords collection is
     * fetched and initialized in the same database transaction.
     *
     * @param id The patient's ID.
     * @return The Patient object with its medicalRecords collection loaded.
     */
    public Patient getByIdWithRecords(int id) {
        return patientDAO.findByIdWithRecords(id);
    }

    /**
     * Counts the number of active patients in the database efficiently.
     * @return The count of active patients.
     */
    public long getActivePatientCount() {
        return patientDAO.countActive();
    }
}