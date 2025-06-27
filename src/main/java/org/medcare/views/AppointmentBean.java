package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Appointment;
import org.medcare.models.Doctor;
import org.medcare.models.Patient;
import org.medcare.service.AppointmentService;
import org.medcare.service.DoctorService;
import org.medcare.service.PatientService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Named
@RequestScoped
public class AppointmentBean {
    private Appointment appointment = new Appointment();
    private List<Appointment> appointments;
    private List<Doctor> doctors;
    private List<Patient> patients;
    private String dateStr;
    private String timeStr;

    @Inject
    private AppointmentService appointmentService;

    @Inject
    private DoctorService doctorService;

    @Inject
    private PatientService patientService;

    @PostConstruct
    public void init() {
        appointments = appointmentService.getAll();
        doctors = doctorService.getAll();
        patients = patientService.getAll();
    }

    public String save() {
        appointment.setDate(LocalDate.parse(dateStr));
        appointment.setTime(LocalTime.parse(timeStr));
        appointmentService.save(appointment);
        return "appointments.xhtml?faces-redirect=true";
    }

    public String delete(int id) {
        Appointment a = appointmentService.getById(id);
        if (a != null) {
            appointmentService.delete(a);
        }
        return "appointments.xhtml?faces-redirect=true";
    }

    // Getters and setters
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Doctor> getDoctors() { return doctors; }
    public List<Patient> getPatients() { return patients; }
    public String getDateStr() { return dateStr; }
    public void setDateStr(String dateStr) { this.dateStr = dateStr; }
    public String getTimeStr() { return timeStr; }
    public void setTimeStr(String timeStr) { this.timeStr = timeStr; }
}
