package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.MedicalRecord;

@ApplicationScoped
public class MedicalRecordDAO extends GenericDAO<MedicalRecord> {
    public MedicalRecordDAO() {
        super(MedicalRecord.class);
    }
}
