package org.medcare.service.interfaces;

import org.medcare.models.Patient;
import org.medcare.models.User;

import java.util.List;

public interface PatientServiceInterface {
    void createPatient(Patient patient, User creator);
    void updatePatient(Patient patient, User updater);
    void softDeletePatient(Patient patient, User deleter);
    void reactivatePatient(Patient patient, User reactivator);
    List<Patient> getAllActive();
    List<Patient> getAllInactive();
    Patient getById(int id);
    Patient getByIdWithRecords(int id);
    long getActivePatientCount();
}