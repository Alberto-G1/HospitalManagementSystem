package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.DoctorDAO;
import org.medcare.models.Doctor;
import java.util.List;

@ApplicationScoped
public class DoctorService {
    @Inject
    private DoctorDAO doctorDAO;

    @Inject
    private UserService userService;

    public void save(Doctor doctor) {
        doctorDAO.save(doctor);
    }

    public void update(Doctor doctor) {
        doctorDAO.update(doctor);
    }

    public void saveOrUpdate(Doctor doctor) {
        doctorDAO.saveOrUpdate(doctor);
    }

    public void softDelete(Doctor doctor) {
        if (doctor != null && doctor.getUser() != null) {
            userService.softDelete(doctor.getUser());
        }
    }

    public void reactivate(Doctor doctor) {
        if (doctor != null && doctor.getUser() != null) {
            userService.reactivateUser(doctor.getUser());
        }
    }

    // Returns only active doctors
    public List<Doctor> getAll() {
        return doctorDAO.findAll();
    }

    // Method to get all doctors including inactive ones
    public List<Doctor> getAllIncludeInactive() {
        return doctorDAO.findAllIncludeInactive();
    }

    // Returns only active doctor
    public Doctor getById(int id) {
        return doctorDAO.findById(id);
    }

    // Method to get doctor by ID including inactive ones
    public Doctor getByIdIncludeInactive(int id) {
        return doctorDAO.findByIdIncludeInactive(id);
    }

    public Doctor findByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    public Doctor findByUserIdIncludeInactive(int userId) {
        return doctorDAO.findByUserIdIncludeInactive(userId);
    }
}