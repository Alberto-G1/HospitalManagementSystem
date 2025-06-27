package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Doctor;
import org.medcare.utils.HibernateUtil;

@ApplicationScoped
public class DoctorDAO extends GenericDAO<Doctor> {
    public DoctorDAO() {
        super(Doctor.class);
    }

    // Method to find a Doctor profile based on the User's ID
    public Doctor findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor d WHERE d.user.userId = :userId", Doctor.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional() // Use optional to avoid errors if not found
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}