package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.DoctorDAO;
import org.medcare.models.Doctor;
import org.medcare.models.User;

import java.util.List;

@ApplicationScoped
public class DoctorService {
    @Inject
    private DoctorDAO doctorDAO;

    @Inject
    private UserService userService;

    @Inject
    private ActivityLogService activityLogService;

    public void saveOrUpdate(Doctor doctor, User adminUser) {
        boolean isNew = doctor.getDoctorId() == 0;
        doctorDAO.saveOrUpdate(doctor);
        if (isNew) {
            activityLogService.log("DOCTOR_CREATED", "Created new doctor profile: " + doctor.getFirstName() + " " + doctor.getLastName(), adminUser);
        } else {
            activityLogService.log("DOCTOR_UPDATED", "Updated doctor profile: " + doctor.getFirstName() + " " + doctor.getLastName(), adminUser);
        }
    }

    public void softDelete(Doctor doctor, User adminUser) {
        if (doctor != null && doctor.getUser() != null) {
            userService.softDelete(doctor.getUser());
            activityLogService.log("DOCTOR_DEACTIVATED", "Deactivated doctor: " + doctor.getFirstName() + " " + doctor.getLastName(), adminUser);
        }
    }

    public void reactivate(Doctor doctor, User adminUser) {
        if (doctor != null && doctor.getUser() != null) {
            userService.reactivateUser(doctor.getUser());
            activityLogService.log("DOCTOR_REACTIVATED", "Reactivated doctor: " + doctor.getFirstName() + " " + doctor.getLastName(), adminUser);
        }
    }

    public List<Doctor> getAll() {
        return doctorDAO.findAll();
    }

    public List<Doctor> getAllIncludeInactive() {
        return doctorDAO.findAllIncludeInactive();
    }

    public Doctor getById(int id) {
        return doctorDAO.findById(id);
    }

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
