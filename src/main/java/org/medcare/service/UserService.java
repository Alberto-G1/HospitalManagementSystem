package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.UserDAO;
import org.medcare.models.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UserDAO userDAO;

    // ... (hashPassword and checkPassword methods remain the same)

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find SHA-256 hashing algorithm", e);
        }
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    public void createUser(User user, String plainPassword) {
        String hashedPassword = hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        userDAO.saveOrUpdate(user);
    }

    // Authenticate now implicitly checks for active users via the DAO method
    public User authenticate(String username, String plainPassword) {
        User found = userDAO.findByUsername(username); // This now finds active users only
        if (found != null && checkPassword(plainPassword, found.getPassword())) {
            return found;
        }
        return null;
    }

    public void softDelete(User user) {
        if (user != null && !"admin".equalsIgnoreCase(user.getUsername())) {
            user.setActive(false);
            userDAO.saveOrUpdate(user);
        }
    }

    // Other methods...
    public User findByUsername(String username) { return userDAO.findByUsername(username); }
    public User findByUsernameIncludeInactive(String username) { return userDAO.findByUsernameIncludeInactive(username); }
    public User findByEmail(String email) { return userDAO.findByEmail(email); }
    public List<User> getAll() { return userDAO.findAll(); }
    public void saveOrUpdate(User user) { userDAO.saveOrUpdate(user); }
    public User findById(int id) { return userDAO.findById(id); }
}