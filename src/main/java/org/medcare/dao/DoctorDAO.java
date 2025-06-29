package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Doctor;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class DoctorDAO extends GenericDAO<Doctor> {
    public DoctorDAO() {
        super(Doctor.class);
    }

    public Doctor findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor d WHERE d.user.userId = :userId AND d.user.active = true", Doctor.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Override to only find active doctors for general listings
    @Override
    public List<Doctor> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Find all doctors whose associated user account is active
            return session.createQuery("FROM Doctor d WHERE d.user.active = true", Doctor.class).list();
        }
    }
}