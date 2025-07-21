package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.MedicalRecordDAO;
import org.medcare.models.Doctor;
import org.medcare.models.MedicalRecord;
import org.medcare.models.Patient;
import org.medcare.models.User;
import org.medcare.service.interfaces.MedicalRecordServiceInterface;

import java.util.List;

@ApplicationScoped
public class MedicalRecordService implements MedicalRecordServiceInterface {

    @Inject private MedicalRecordDAO medicalRecordDAO;
    @Inject private ActivityLogService activityLogService;

    @Override
    public void addMedicalRecord(Patient patient, Doctor doctor, String notes, String prescription, User user) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setNotes(notes);
        record.setPrescription(prescription);
        record.setCreatedBy(user);

        medicalRecordDAO.save(record);

        activityLogService.log("MEDICAL_RECORD_ADDED", "Added new record for patient: " + patient.getFirstName() + " " + patient.getLastName(), user);
    }

    @Override
    public List<MedicalRecord> getRecordsForPatient(int patientId) {
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        return medicalRecordDAO.findByPatient(patient);
    }

    @Override
    public List<MedicalRecord> getRecordsByPatient(Patient patient) {
        return medicalRecordDAO.findByPatient(patient);
    }
}
