package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.MedicalRecord;
import org.medcare.models.Patient;
import org.medcare.utils.HibernateUtil;

import java.util.List;

@ApplicationScoped
public class MedicalRecordDAO extends GenericDAO<MedicalRecord> {
    public MedicalRecordDAO() {
        super(MedicalRecord.class);
    }

    public List<MedicalRecord> findByPatient(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM MedicalRecord r WHERE r.patient = :patient ORDER BY r.visitDate DESC", MedicalRecord.class)
                    .setParameter("patient", patient)
                    .list();
        }
    }
}
