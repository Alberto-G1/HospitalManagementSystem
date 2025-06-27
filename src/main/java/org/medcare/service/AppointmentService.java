package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.AppointmentDAO;
import org.medcare.models.Appointment;

import java.util.List;

@ApplicationScoped
public class AppointmentService {
    @Inject
    private AppointmentDAO appointmentDAO;

    public void save(Appointment appointment) {
        appointmentDAO.saveOrUpdate(appointment);
    }

    public void delete(Appointment appointment) {
        appointmentDAO.delete(appointment);
    }

    public List<Appointment> getAll() {
        return appointmentDAO.findAll();
    }

    public Appointment getById(int id) {
        return appointmentDAO.findById(id);
    }
}
