package org.medcare.service.interfaces;

import org.medcare.models.Doctor;
import org.medcare.models.User;

import java.util.List;

public interface DoctorServiceInterface {
    void saveOrUpdate(Doctor doctor, User adminUser);
    void softDelete(Doctor doctor, User adminUser);
    void reactivate(Doctor doctor, User adminUser);
    List<Doctor> getAll();
    List<Doctor> getAllIncludeInactive();
    Doctor getById(int id);
    Doctor getByIdIncludeInactive(int id);
    Doctor findByUserId(int userId);
    Doctor findByUserIdIncludeInactive(int userId);
}
