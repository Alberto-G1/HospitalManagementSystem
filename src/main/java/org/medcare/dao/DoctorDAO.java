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

    // Method to find doctor by user ID including inactive ones
    public Doctor findByUserIdIncludeInactive(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor d WHERE d.user.userId = :userId", Doctor.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Override to only find active doctors for general listings
    @Override
    public List<Doctor> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor d WHERE d.user.active = true", Doctor.class).list();
        }
    }

    // Method to find all doctors including inactive ones
    public List<Doctor> findAllIncludeInactive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor d", Doctor.class).list();
        }
    }

    // Override findById to only return active doctors
    @Override
    public Doctor findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Doctor d WHERE d.doctorId = :id AND d.user.active = true", Doctor.class)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Method to find by ID including inactive doctors
    public Doctor findByIdIncludeInactive(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Doctor.class, id);
        }
    }
}