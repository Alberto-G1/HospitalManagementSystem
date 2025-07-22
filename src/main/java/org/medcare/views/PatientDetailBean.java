package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Appointment;
import org.medcare.models.MedicalRecord;
import org.medcare.models.Patient;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.service.interfaces.PatientServiceInterface;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class PatientDetailBean implements Serializable {

    @Inject private PatientServiceInterface patientService;
    @Inject private AppointmentServiceInterface appointmentService;

    private int patientId;
    private Patient patient;
    private List<MedicalRecord> medicalRecords;
    private List<Appointment> appointments;

    /**
     * Called by f:viewAction. Loads the patient and all related data.
     */
    public void loadPatient() {
        if (patientId > 0) {
            this.patient = patientService.getByIdWithRecords(patientId);
            if (this.patient != null) {
                // Eager fetching ensures medicalRecords is available
                this.medicalRecords = this.patient.getMedicalRecords().stream()
                        .sorted(Comparator.comparing(MedicalRecord::getVisitDate).reversed())
                        .collect(Collectors.toList());

                // Fetch appointments separately
                this.appointments = appointmentService.getAllActive().stream()
                        .filter(a -> a.getPatient().getPatientId() == patientId)
                        .sorted(Comparator.comparing(Appointment::getDate).reversed())
                        .collect(Collectors.toList());
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Patient not found.");
            }
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public Patient getPatient() { return patient; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public List<Appointment> getAppointments() { return appointments; }
}