package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.ReceptionistDAO;
import org.medcare.models.Receptionist;
import org.medcare.models.User;

import java.util.List;

@ApplicationScoped
public class ReceptionistService {

    @Inject
    private ReceptionistDAO receptionistDAO;

    @Inject
    private UserService userService;

    @Inject
    private ActivityLogService activityLogService;

    public void saveOrUpdate(Receptionist receptionist, User adminUser) {
        boolean isNew = receptionist.getReceptionistId() == 0;
        receptionistDAO.saveOrUpdate(receptionist);
        if (isNew) {
            activityLogService.log("RECEPTIONIST_CREATED", "Created new receptionist: " + receptionist.getFirstName(), adminUser);
        } else {
            activityLogService.log("RECEPTIONIST_UPDATED", "Updated receptionist: " + receptionist.getFirstName(), adminUser);
        }
    }

    public void softDelete(Receptionist receptionist, User adminUser) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.softDelete(receptionist.getUser());
            activityLogService.log("RECEPTIONIST_DEACTIVATED", "Deactivated receptionist: " + receptionist.getFirstName(), adminUser);
        }
    }

    public void reactivate(Receptionist receptionist, User adminUser) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.reactivateUser(receptionist.getUser());
            activityLogService.log("RECEPTIONIST_REACTIVATED", "Reactivated receptionist: " + receptionist.getFirstName(), adminUser);
        }
    }

    public List<Receptionist> getAll() {
        return receptionistDAO.findAll();
    }

    public List<Receptionist> getAllIncludeInactive() {
        return receptionistDAO.findAllIncludeInactive();
    }

    public Receptionist getById(int id) {
        return receptionistDAO.findById(id);
    }

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
