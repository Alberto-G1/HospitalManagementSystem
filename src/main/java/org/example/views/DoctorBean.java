package org.example.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.example.models.Doctor;
import org.example.service.DoctorService;

import java.util.List;

@Named
@RequestScoped
public class DoctorBean {
    private Doctor doctor = new Doctor();
    private final DoctorService doctorService = new DoctorService();

    private List<Doctor> doctors;

    @PostConstruct
    public void init() {
        doctors = doctorService.getAllDoctors();
    }

    public String add() {
        doctorService.addDoctor(doctor);
        return "doctors.xhtml?faces-redirect=true";
    }

    public String delete(int id) {
        Doctor d = doctorService.getDoctor(id);
        if (d != null) {
            doctorService.deleteDoctor(d);
        }
        return "doctors.xhtml?faces-redirect=true";
    }

    // Getters and Setters
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public List<Doctor> getDoctors() { return doctors; }
}
