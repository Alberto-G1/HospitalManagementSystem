package org.example.controller;

import org.example.models.Doctor;
import org.example.service.DoctorService;

import java.util.List;

public class DoctorController {
    private final DoctorService doctorService = new DoctorService();

    public void addDoctor(Doctor doctor) {
        doctorService.addDoctor(doctor);
    }

    public void deleteDoctor(int id) {
        Doctor d = doctorService.getDoctor(id);
        if (d != null) {
            doctorService.deleteDoctor(d);
            System.out.println("✅ Doctor deleted.");
        } else {
            System.out.println("❌ Doctor not found.");
        }
    }

    public List<Doctor> listDoctors() {
        return doctorService.getAllDoctors();
    }
}
