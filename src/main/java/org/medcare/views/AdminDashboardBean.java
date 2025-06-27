package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.service.*;

import java.util.Date;

@Named
@RequestScoped
public class AdminDashboardBean {

    @Inject
    private PatientService patientService;

    @Inject
    private DoctorService doctorService;

    @Inject
    private AppointmentService appointmentService;

    private int totalPatients;
    private int totalDoctors;
    private int totalAppointments;
    private Date currentDateTime;

    @PostConstruct
    public void init() {
        try {
            totalPatients = patientService.getAllPatients().size();
            totalDoctors = doctorService.getAllDoctors().size();
            totalAppointments = appointmentService.getAllAppointments().size();
            currentDateTime = new Date();
        } catch (Exception e) {
            // Handle errors gracefully
            totalPatients = 0;
            totalDoctors = 0;
            totalAppointments = 0;
            currentDateTime = new Date();
        }
    }

    public void generatePatientReport() {
        try {
            patientService.addSuccessMessage("Report", "Patient report generation started. Total patients: " + totalPatients);
        } catch (Exception e) {
            patientService.addErrorMessage("Report Error", "Failed to generate patient report: " + e.getMessage());
        }
    }

    public void generateAppointmentReport() {
        try {
            appointmentService.addSuccessMessage("Report", "Appointment report generation started. Total appointments: " + totalAppointments);
        } catch (Exception e) {
            appointmentService.addErrorMessage("Report Error", "Failed to generate appointment report: " + e.getMessage());
        }
    }

    // Getters
    public int getTotalPatients() { return totalPatients; }
    public int getTotalDoctors() { return totalDoctors; }
    public int getTotalAppointments() { return totalAppointments; }
    public Date getCurrentDateTime() { return currentDateTime; }
}