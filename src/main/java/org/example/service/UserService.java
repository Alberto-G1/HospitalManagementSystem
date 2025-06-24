package org.example.service;

import org.example.dao.UserDAO;
import org.example.models.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public void registerUser(User user) {
        userDAO.save(user);
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
}
