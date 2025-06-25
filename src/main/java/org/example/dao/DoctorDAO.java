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

    public List<Doctor> findAvailableDoctors() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE isAvailable = :available", Doctor.class);
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
            String hql = "FROM Doctor d WHERE " +
                    "LOWER(d.firstName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(d.lastName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(d.speciality) LIKE LOWER(:searchTerm) OR " +
                    "d.phoneNumber LIKE :searchTerm OR " +
                    "LOWER(d.email) LIKE LOWER(:searchTerm)";

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
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE LOWER(speciality) = LOWER(:speciality)", Doctor.class);
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
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE LOWER(email) = LOWER(:email)", Doctor.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}