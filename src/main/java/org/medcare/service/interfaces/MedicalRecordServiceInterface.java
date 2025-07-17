package org.medcare.service.interfaces;

import org.medcare.models.Doctor;
import org.medcare.models.MedicalRecord;
import org.medcare.models.Patient;
import org.medcare.models.User;

import java.util.List;

public interface MedicalRecordServiceInterface {
    void addMedicalRecord(Patient patient, Doctor doctor, String notes, String prescription, User creator);
    List<MedicalRecord> getRecordsForPatient(int patientId);
}