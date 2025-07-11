package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.MedicalRecordDAO;
import org.medcare.models.Doctor;
import org.medcare.models.MedicalRecord;
import org.medcare.models.Patient;
import org.medcare.models.User;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class MedicalRecordService {
    @Inject
    private MedicalRecordDAO recordDAO;
    @Inject
    private ActivityLogService activityLogService;

    public void addMedicalRecord(Patient patient, Doctor doctor, String notes, String prescription, User creator) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setNotes(notes);
        record.setPrescription(prescription);
        record.setVisitDate(LocalDateTime.now());
        record.setCreatedBy(creator);
        recordDAO.save(record);
        activityLogService.log("MEDICAL_RECORD_ADDED", "Added medical record for patient " + patient.getFirstName() + " by Dr. " + doctor.getLastName(), creator);
    }

    public List<MedicalRecord> getRecordsForPatient(int patientId) {
        // This is a simplification. A dedicated DAO method would be better.
        // For now, we'll filter in the bean. In a real app, add:
        // return recordDAO.findByPatientId(patientId);
        return null; // Placeholder
    }
}