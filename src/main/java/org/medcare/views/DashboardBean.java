package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.service.AppointmentService;
import org.medcare.service.DoctorService;
import org.medcare.service.PatientService;

@Named
@RequestScoped
public class DashboardBean {

    @Inject
    private PatientService patientService;
    @Inject
    private DoctorService doctorService;
    @Inject
    private AppointmentService appointmentService;

    private long patientCount;
    private long doctorCount;
    private long appointmentCount;

    @PostConstruct
    public void init() {
        // Fetch the counts from the services
        patientCount = patientService.getAll().size();
        doctorCount = doctorService.getAll().size();
        appointmentCount = appointmentService.getAll().size();
    }

    // Getters for the counts
    public long getPatientCount() {
        return patientCount;
    }

    public long getDoctorCount() {
        return doctorCount;
    }

    public long getAppointmentCount() {
        return appointmentCount;
    }
}