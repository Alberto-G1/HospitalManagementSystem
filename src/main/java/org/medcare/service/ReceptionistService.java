package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.ReceptionistDAO;
import org.medcare.models.Receptionist;
import java.util.List;

@ApplicationScoped
public class ReceptionistService {

    @Inject
    private ReceptionistDAO receptionistDAO;

    @Inject
    private UserService userService;

    public void save(Receptionist receptionist) {
        receptionistDAO.save(receptionist);
    }

    public void update(Receptionist receptionist) {
        receptionistDAO.update(receptionist);
    }

    public void saveOrUpdate(Receptionist receptionist) {
        receptionistDAO.saveOrUpdate(receptionist);
    }

    public void softDelete(Receptionist receptionist) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.softDelete(receptionist.getUser());
        }
    }

    public void reactivate(Receptionist receptionist) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.reactivateUser(receptionist.getUser());
        }
    }

    // Returns only active receptionists
    public List<Receptionist> getAll() {
        return receptionistDAO.findAll();
    }

    // Method to get all receptionists including inactive ones
    public List<Receptionist> getAllIncludeInactive() {
        return receptionistDAO.findAllIncludeInactive();
    }

    // Returns only active receptionist
    public Receptionist getById(int id) {
        return receptionistDAO.findById(id);
    }

    // Method to get receptionist by ID including inactive ones
    public Receptionist getByIdIncludeInactive(int id) {
        return receptionistDAO.findByIdIncludeInactive(id);
    }

    public Receptionist findByUserId(int userId) {
        return receptionistDAO.findByUserId(userId);
    }

    public Receptionist findByUserIdIncludeInactive(int userId) {
        return receptionistDAO.findByUserIdIncludeInactive(userId);
    }
}