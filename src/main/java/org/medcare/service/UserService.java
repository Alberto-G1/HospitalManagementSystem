package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.UserDAO;
import org.medcare.models.User;

import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    private UserDAO userDAO;

    public void register(User user) {
        userDAO.saveOrUpdate(user);
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public List<User> getAll() {
        return userDAO.findAll();
    }
}
