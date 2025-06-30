package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Patient;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class PatientDAO extends GenericDAO<Patient> {
    public PatientDAO() { super(Patient.class); }

    // Override to only find active patients for general listings
    @Override
    public List<Patient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Patient WHERE active = true ORDER BY lastName, firstName", Patient.class).list();
        }
    }
}