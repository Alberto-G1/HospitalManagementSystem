package org.example.controller;

import org.example.models.Appointment;
import org.example.service.AppointmentService;

import java.util.List;

public class AppointmentController {
    private final AppointmentService appointmentService = new AppointmentService();

    public void bookAppointment(Appointment appointment) {
        appointmentService.bookAppointment(appointment);
    }

    public List<Appointment> listAppointments() {
        return appointmentService.getAllAppointments();
    }

    public void cancelAppointment(int id) {
        Appointment a = appointmentService.getAppointment(id);
        if (a != null) {
            appointmentService.cancelAppointment(a);
            System.out.println("✅ Appointment cancelled.");
        } else {
            System.out.println("❌ Appointment not found.");
        }
    }
}
