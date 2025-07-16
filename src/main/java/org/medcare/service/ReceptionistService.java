package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.ReceptionistDAO;
import org.medcare.models.Receptionist;
import org.medcare.models.User;
import org.medcare.service.interfaces.ReceptionistServiceInterface;

import java.util.List;

@ApplicationScoped
public class ReceptionistService implements ReceptionistServiceInterface {

    @Inject private ReceptionistDAO receptionistDAO;
    @Inject private UserService userService;
    @Inject private ActivityLogService activityLogService;

    @Override
    public void saveOrUpdate(Receptionist receptionist, User adminUser) {
        boolean isNew = receptionist.getReceptionistId() == 0;
        receptionistDAO.saveOrUpdate(receptionist);
        if (isNew) {
            activityLogService.log("RECEPTIONIST_CREATED", "Created new receptionist: " + receptionist.getFirstName(), adminUser);
        } else {
            activityLogService.log("RECEPTIONIST_UPDATED", "Updated receptionist: " + receptionist.getFirstName(), adminUser);
        }
    }

    @Override
    public void softDelete(Receptionist receptionist, User adminUser) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.softDelete(receptionist.getUser());
            activityLogService.log("RECEPTIONIST_DEACTIVATED", "Deactivated receptionist: " + receptionist.getFirstName(), adminUser);
        }
    }

    @Override
    public void reactivate(Receptionist receptionist, User adminUser) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.reactivateUser(receptionist.getUser());
            activityLogService.log("RECEPTIONIST_REACTIVATED", "Reactivated receptionist: " + receptionist.getFirstName(), adminUser);
        }
    }

    @Override
    public List<Receptionist> getAll() {
        return receptionistDAO.findAll();
    }

    @Override
    public List<Receptionist> getAllIncludeInactive() {
        return receptionistDAO.findAllIncludeInactive();
    }

    @Override
    public Receptionist getById(int id) {
        return receptionistDAO.findById(id);
    }

    @Override
    public Receptionist getByIdIncludeInactive(int id) {
        return receptionistDAO.findByIdIncludeInactive(id);
    }

    @Override
    public Receptionist findByUserId(int userId) {
        return receptionistDAO.findByUserId(userId);
    }

    @Override
    public Receptionist findByUserIdIncludeInactive(int userId) {
        return receptionistDAO.findByUserIdIncludeInactive(userId);
    }
}