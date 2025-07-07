package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Appointment;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class AppointmentDAO extends GenericDAO<Appointment> {
    public AppointmentDAO() { super(Appointment.class); }

    @Override
    public List<Appointment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT a FROM Appointment a " +
                            "JOIN FETCH a.patient " +
                            "JOIN FETCH a.doctor d " +
                            "JOIN FETCH d.user " + // Also fetch the user associated with the doctor
                            "LEFT JOIN FETCH a.createdBy " +
                            "ORDER BY a.date, a.time",
                    Appointment.class
            ).list();
        }
    }

    public List<Appointment> findByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT a FROM Appointment a " +
                            "JOIN FETCH a.patient " +
                            "JOIN FETCH a.doctor d " +
                            "JOIN FETCH d.user " +
                            "LEFT JOIN FETCH a.createdBy " +
                            "WHERE a.doctor.doctorId = :doctorId " +
                            "ORDER BY a.date, a.time",
                    Appointment.class
            ).setParameter("doctorId", doctorId).list();
        }
    }
}