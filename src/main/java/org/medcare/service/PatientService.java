package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.medcare.dao.PatientArchiveDAO;
import org.medcare.dao.PatientDAO;
import org.medcare.models.Patient;
import org.medcare.models.PatientArchive;
import org.medcare.models.User;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class PatientService {

    @Inject private PatientDAO patientDAO;
    @Inject private PatientArchiveDAO archiveDAO;

    public void createPatient(Patient patient, User creator) {
        patient.setCreatedBy(creator);
        patient.setActive(true); // Ensure new patients are active
        patientDAO.saveOrUpdate(patient);
    }

    public void updatePatient(Patient patient, User updater) {
        patient.setLastUpdatedBy(updater);
        patientDAO.saveOrUpdate(patient);
    }

    public void archivePatient(int patientId, User archiver) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Patient patient = session.get(Patient.class, patientId);
            if (patient != null) {
                // We are not using a soft-delete `active` flag here,
                // but moving the record entirely, which is a better form of "soft delete".
                PatientArchive archive = new PatientArchive(patient, archiver);
                session.save(archive);
                session.delete(patient);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Patient> getAllActive() {
        return patientDAO.findAll();
    }

    public List<PatientArchive> getAllArchived() {
        return archiveDAO.findAll();
    }

    public Patient getById(int id) {
        return patientDAO.findById(id);
    }
}