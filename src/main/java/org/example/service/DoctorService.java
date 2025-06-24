package org.example.service;

import org.example.dao.DoctorDAO;
import org.example.models.Doctor;

import java.util.List;

public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public void addDoctor(Doctor doctor) {
        doctorDAO.save(doctor);
    }

    public void deleteDoctor(Doctor doctor) {
        doctorDAO.delete(doctor);
    }

    public Doctor getDoctor(int id) {
        return doctorDAO.findById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAll();
    }
}
