package org.example.dao;

import org.example.enums.Availability;
import org.example.models.Doctor;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class DoctorDAO extends GenericDAO<Doctor> {
    public DoctorDAO() {
        super(Doctor.class);
    }

    @Override
    public List<Doctor> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE deleted = false ORDER BY lastName, firstName", Doctor.class);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Doctor findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE doctorID = :id AND deleted = false", Doctor.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Doctor> findAvailableDoctors() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE isAvailable = :available AND deleted = false ORDER BY lastName, firstName", Doctor.class);
            query.setParameter("available", Availability.AVAILABLE);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Doctor> searchDoctors(String searchTerm) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Doctor d WHERE d.deleted = false AND (" +
                    "LOWER(d.firstName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(d.lastName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(d.speciality) LIKE LOWER(:searchTerm) OR " +
                    "d.phoneNumber LIKE :searchTerm OR " +
                    "LOWER(d.email) LIKE LOWER(:searchTerm)) " +
                    "ORDER BY d.lastName, d.firstName";

            Query<Doctor> query = session.createQuery(hql, Doctor.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");

            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Doctor> findBySpeciality(String speciality) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE LOWER(speciality) = LOWER(:speciality) AND deleted = false ORDER BY lastName, firstName", Doctor.class);
            query.setParameter("speciality", speciality);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Doctor findByEmail(String email) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE LOWER(email) = LOWER(:email) AND deleted = false", Doctor.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Doctor findByEmailExcludingId(String email, int excludeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE LOWER(email) = LOWER(:email) AND doctorID != :excludeId AND deleted = false", Doctor.class);
            query.setParameter("email", email);
            query.setParameter("excludeId", excludeId);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Doctor findByPhoneExcludingId(String phone, int excludeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE phoneNumber = :phone AND doctorID != :excludeId AND deleted = false", Doctor.class);
            query.setParameter("phone", phone);
            query.setParameter("excludeId", excludeId);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // Soft delete method
    public void softDelete(Doctor doctor) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            doctor.setDeleted(true);
            doctor.setIsAvailable(Availability.UNAVAILABLE);
            session.merge(doctor);

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}