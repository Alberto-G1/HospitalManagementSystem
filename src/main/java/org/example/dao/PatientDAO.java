package org.example.dao;

import org.example.models.Patient;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PatientDAO extends GenericDAO<Patient> {
    public PatientDAO() {
        super(Patient.class);
    }

    public List<Patient> searchPatients(String searchTerm) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Patient p WHERE " +
                    "LOWER(p.firstName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(p.lastName) LIKE LOWER(:searchTerm) OR " +
                    "p.phoneNumber LIKE :searchTerm OR " +
                    "LOWER(p.email) LIKE LOWER(:searchTerm)";

            Query<Patient> query = session.createQuery(hql, Patient.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");

            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Patient findByPhoneNumber(String phoneNumber) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Patient> query = session.createQuery("FROM Patient WHERE phoneNumber = :phone", Patient.class);
            query.setParameter("phone", phoneNumber);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Patient findByEmail(String email) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Patient> query = session.createQuery("FROM Patient WHERE LOWER(email) = LOWER(:email)", Patient.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}