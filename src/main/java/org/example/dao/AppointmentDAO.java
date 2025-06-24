package org.example.dao;

import org.example.models.Appointment;

public class AppointmentDAO extends GenericDAO<Appointment> {
    public AppointmentDAO() {
        super(Appointment.class);
    }
}
