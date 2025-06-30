package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Appointment;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class AppointmentDAO extends GenericDAO<Appointment> {
    public AppointmentDAO() { super(Appointment.class); }

    // New method to get appointments for a specific doctor
    public List<Appointment> findByDoctorId(int doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Appointment a WHERE a.doctor.doctorId = :doctorId ORDER BY a.date, a.time", Appointment.class)
                    .setParameter("doctorId", doctorId)
                    .list();
        }
    }
}