package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Patient;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class PatientDAO extends GenericDAO<Patient> {
    public PatientDAO() {
        super(Patient.class);
    }

    /**
     * Finds all patients and eagerly fetches their associated medical records.
     * This prevents LazyInitializationException.
     */
    @Override
    public List<Patient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Using LEFT JOIN FETCH ensures we get patients even if they have no records.
            return session.createQuery(
                            "SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.medicalRecords", Patient.class)
                    .list();
        }
    }

    /**
     * Finds a single patient by ID and eagerly fetches their medical records.
     * @param id The patient ID.
     * @return The Patient object with records initialized, or null if not found.
     */
    public Patient findByIdWithRecords(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT p FROM Patient p LEFT JOIN FETCH p.medicalRecords WHERE p.patientId = :id", Patient.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public long countActive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT count(p) FROM Patient p WHERE p.active = true", Long.class)
                    .getSingleResult();
        }
    }
}