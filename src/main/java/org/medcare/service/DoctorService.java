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
        doctorDAO.saveOrUpdate(doctor);
    }

    public void softDelete(Doctor doctor) {
        if (doctor != null && doctor.getUser() != null) {
            userService.softDelete(doctor.getUser());
        }
    }

    public List<Doctor> getAll() {
        return doctorDAO.findAll();
    }

    public Doctor getById(int id) {
        return doctorDAO.findById(id);
    }

    public Doctor findByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }
}