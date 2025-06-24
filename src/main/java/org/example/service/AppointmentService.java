package org.example.service;

import org.example.dao.AppointmentDAO;
import org.example.models.Appointment;

import java.util.List;

public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void bookAppointment(Appointment appointment) {
        appointmentDAO.save(appointment);
    }

    public void cancelAppointment(Appointment appointment) {
        appointmentDAO.delete(appointment);
    }

    public Appointment getAppointment(int id) {
        return appointmentDAO.findById(id);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }
}
