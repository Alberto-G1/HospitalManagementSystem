package org.medcare.service.interfaces;

import org.medcare.models.Receptionist;
import org.medcare.models.User;

import java.util.List;

public interface ReceptionistServiceInterface {
    void saveOrUpdate(Receptionist receptionist, User adminUser);
    void softDelete(Receptionist receptionist, User adminUser);
    void reactivate(Receptionist receptionist, User adminUser);
    List<Receptionist> getAll();
    List<Receptionist> getAllIncludeInactive();
    Receptionist getById(int id);
    Receptionist getByIdIncludeInactive(int id);
    Receptionist findByUserId(int userId);
    Receptionist findByUserIdIncludeInactive(int userId);
}