package org.example.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.example.models.Appointment;
import org.example.models.Doctor;
import org.example.models.Patient;
import org.example.service.AppointmentService;
import org.example.service.DoctorService;
import org.example.service.PatientService;

import java.util.List;

@Named
@RequestScoped
public class AppointmentBean {
    private Appointment appointment = new Appointment();
    private final AppointmentService appointmentService = new AppointmentService();
    private final DoctorService doctorService = new DoctorService();
    private final PatientService patientService = new PatientService();

    private List<Appointment> appointments;
    private List<Doctor> doctors;
    private List<Patient> patients;

    @PostConstruct
    public void init() {
        appointments = appointmentService.getAllAppointments();
        doctors = doctorService.getAllDoctors();
        patients = patientService.getAllPatients();
    }

    public String book() {
        appointmentService.bookAppointment(appointment);
        return "appointments.xhtml?faces-redirect=true";
    }

    public String cancel(int id) {
        Appointment a = appointmentService.getAppointment(id);
        if (a != null) {
            appointmentService.cancelAppointment(a);
        }
        return "appointments.xhtml?faces-redirect=true";
    }

    // Getters and Setters
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Doctor> getDoctors() { return doctors; }
    public List<Patient> getPatients() { return patients; }
}
