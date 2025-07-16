package org.medcare.service.interfaces;

import org.medcare.enums.AppointmentStatus;
import org.medcare.models.Appointment;
import org.medcare.models.AppointmentArchive;
import org.medcare.models.User;

import java.util.List;

public interface AppointmentServiceInterface {
    void createAppointment(Appointment appointment, User creator);
    void updateAppointmentStatus(Appointment appointment, AppointmentStatus newStatus, User updater);
    void archiveAppointment(int appointmentId, User archiver);
    List<Appointment> getAllActive();
    List<AppointmentArchive> getAllArchived();
    List<Appointment> getAppointmentsForDoctor(int doctorId);
    Appointment getById(int id);
}