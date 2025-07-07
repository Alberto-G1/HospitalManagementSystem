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
        receptionistDAO.saveOrUpdate(receptionist);
    }

    public void softDelete(Receptionist receptionist) {
        if (receptionist != null && receptionist.getUser() != null) {
            userService.softDelete(receptionist.getUser());
        }
    }

    public List<Receptionist> getAll() {
        return receptionistDAO.findAll();
    }

    public Receptionist getById(int id) {
        return receptionistDAO.findById(id);
    }

    public Receptionist findByUserId(int userId) {
        return receptionistDAO.findByUserId(userId);
    }
}