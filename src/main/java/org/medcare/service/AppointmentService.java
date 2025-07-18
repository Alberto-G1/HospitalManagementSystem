package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.AppointmentDAO;
import org.medcare.dao.AppointmentArchiveDAO;
import org.medcare.enums.AppointmentStatus;
import org.medcare.models.Appointment;
import org.medcare.models.AppointmentArchive;
import org.medcare.models.User;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@ApplicationScoped
public class AppointmentService implements AppointmentServiceInterface {

    @Inject private AppointmentDAO appointmentDAO;
    @Inject private AppointmentArchiveDAO archiveDAO;
    @Inject private ActivityLogService activityLogService;

    @Override
    public void createAppointment(Appointment appointment, User creator) {
        appointment.setCreatedBy(creator);
        appointment.setLastUpdatedBy(creator);
        appointmentDAO.saveOrUpdate(appointment);
        activityLogService.log("APPOINTMENT_CREATED", "Booked appointment for " + appointment.getPatient().getFirstName() + " with Dr. " + appointment.getDoctor().getLastName(), creator);
    }

    @Override
    public void updateAppointmentStatus(Appointment appointment, AppointmentStatus newStatus, User updater) {
        appointment.setStatus(newStatus);
        appointment.setLastUpdatedBy(updater);
        appointmentDAO.saveOrUpdate(appointment);
        activityLogService.log("APPOINTMENT_STATUS_UPDATED", "Status for appointment ID " + appointment.getAppointmentId() + " changed to " + newStatus, updater);
    }

    @Override
    public void archiveAppointment(int appointmentId, User archiver) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, appointmentId);
            if (appointment != null) {
                AppointmentArchive archive = new AppointmentArchive(appointment, archiver);
                session.persist(archive);
                activityLogService.log("APPOINTMENT_ARCHIVED", "Archived appointment ID " + appointment.getAppointmentId(), archiver);
                session.remove(appointment);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to archive appointment", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Appointment> getAllActive() {
        return appointmentDAO.findAll();
    }

    @Override
    public List<AppointmentArchive> getAllArchived() {
        return archiveDAO.findAll();
    }

    @Override
    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        return appointmentDAO.findByDoctorId(doctorId);
    }

    @Override
    public Appointment getById(int id) {
        return appointmentDAO.findById(id);
    }
}
