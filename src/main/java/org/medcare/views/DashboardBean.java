package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.service.AppointmentService;
import org.medcare.service.DoctorService;
import org.medcare.service.PatientService;
import org.medcare.service.ReceptionistService; // 1. IMPORT THE SERVICE

@Named
@RequestScoped
public class DashboardBean {

    @Inject
    private PatientService patientService;
    @Inject
    private DoctorService doctorService;
    @Inject
    private AppointmentService appointmentService;
    @Inject
    private ReceptionistService receptionistService; // 2. INJECT THE SERVICE

    private long patientCount;
    private long doctorCount;
    private long receptionistCount; // 3. ADD A FIELD FOR THE COUNT
    private long appointmentCount;

    @PostConstruct
    public void init() {
        // Fetch all the counts from the services
        patientCount = patientService.getAllActive().size();
        doctorCount = doctorService.getAll().size();
        receptionistCount = receptionistService.getAll().size(); // 4. INITIALIZE THE COUNT
        appointmentCount = appointmentService.getAllActive().size();
    }

    // Getters for the counts
    public long getPatientCount() {
        return patientCount;
    }

    public long getDoctorCount() {
        return doctorCount;
    }

    // 5. ADD A GETTER FOR THE NEW COUNT
    public long getReceptionistCount() {
        return receptionistCount;
    }

    public long getAppointmentCount() {
        return appointmentCount;
    }
}