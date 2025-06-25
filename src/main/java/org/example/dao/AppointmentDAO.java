package org.example.dao;

import org.example.enums.AppointmentStatus;
import org.example.models.Appointment;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AppointmentDAO extends GenericDAO<Appointment> {
    public AppointmentDAO() {
        super(Appointment.class);
    }

    public List<Appointment> findByDoctor(int doctorId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE doctor.doctorID = :doctorId ORDER BY appointmentDate DESC, appointmentTime DESC",
                    Appointment.class);
            query.setParameter("doctorId", doctorId);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> findByPatient(int patientId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE patient.patientID = :patientId ORDER BY appointmentDate DESC, appointmentTime DESC",
                    Appointment.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> findByDate(Date appointmentDate) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE appointmentDate = :date ORDER BY appointmentTime",
                    Appointment.class);
            query.setParameter("date", appointmentDate);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> findByDoctorAndDate(int doctorId, Date appointmentDate) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE doctor.doctorID = :doctorId AND appointmentDate = :date",
                    Appointment.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("date", appointmentDate);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE status = :status ORDER BY appointmentDate, appointmentTime",
                    Appointment.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> findUpcomingAppointments() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE appointmentDate >= :today AND status = :status ORDER BY appointmentDate, appointmentTime",
                    Appointment.class);
            query.setParameter("today", Date.valueOf(LocalDate.now()));
            query.setParameter("status", AppointmentStatus.SCHEDULED);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> searchAppointments(String searchTerm) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Appointment a WHERE " +
                    "LOWER(a.patient.firstName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(a.patient.lastName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(a.doctor.firstName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(a.doctor.lastName) LIKE LOWER(:searchTerm) OR " +
                    "LOWER(a.reason) LIKE LOWER(:searchTerm) OR " +
                    "CAST(a.appointmentDate AS string) LIKE :searchTerm";

            Query<Appointment> query = session.createQuery(hql, Appointment.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");

            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Appointment> findByDateRange(Date startDate, Date endDate) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE appointmentDate BETWEEN :startDate AND :endDate ORDER BY appointmentDate, appointmentTime",
                    Appointment.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public long countAppointmentsByStatus(AppointmentStatus status) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Appointment WHERE status = :status",
                    Long.class);
            query.setParameter("status", status);
            return query.uniqueResult();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}