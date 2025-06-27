package org.medcare.dao;
import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.Appointment;

@ApplicationScoped
public class AppointmentDAO extends GenericDAO<Appointment> {
    public AppointmentDAO() { super(Appointment.class); }
}
