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

    @Inject private UserDAO userDAO;
    @Inject private ActivityLogService activityLogService;

    public UserService() {
        // CDI constructor
    }

    public UserService(boolean manual) {
        if (manual) {
            this.userDAO = new UserDAO();
        }
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find SHA-256 hashing algorithm", e);
        }
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    public void createUser(User user, String plainPassword) {
        String hashedPassword = hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        userDAO.save(user);
        // Note: Logging is done in Doctor/Receptionist services to be more specific
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (checkPassword(oldPassword, user.getPassword())) {
            user.setPassword(hashPassword(newPassword));
            userDAO.update(user);
            activityLogService.log("PASSWORD_CHANGE", "User changed their own password.", user);
            return true;
        }
        return false;
    }

    public void adminResetPassword(User user, String newPassword, User admin) {
        user.setPassword(hashPassword(newPassword));
        userDAO.update(user);
        activityLogService.log("PASSWORD_RESET", "Admin reset password for user: " + user.getUsername(), admin);
    }


    public void updateUser(User user) {
        userDAO.update(user);
    }

    public User authenticate(String username, String plainPassword) {
        User found = userDAO.findByUsername(username);
        if (found != null && checkPassword(plainPassword, found.getPassword())) {
            activityLogService.log("LOGIN_SUCCESS", "User logged in successfully.", found);
            return found;
        }
        if (found != null) {
            activityLogService.log("LOGIN_FAILED", "User login failed (invalid password).", found);
        }
        return null;
    }

    public void softDelete(User user) {
        if (user != null && !"admin".equalsIgnoreCase(user.getUsername())) {
            userDAO.updateUserActiveStatus(user.getUserId(), false);
        }
    }

    public void reactivateUser(User user) {
        if (user != null) {
            userDAO.updateUserActiveStatus(user.getUserId(), true);
        }
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User findByUsernameIncludeInactive(String username) {
        return userDAO.findByUsernameIncludeInactive(username);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public User findByEmailIncludeInactive(String email) {
        return userDAO.findByEmailIncludeInactive(email);
    }

    public List<User> getAll() {
        return userDAO.findAll();
    }

    public List<User> getAllIncludeInactive() {
        return userDAO.findAllIncludeInactive();
    }

    public void saveOrUpdate(User user) {
        userDAO.saveOrUpdate(user);
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }
}