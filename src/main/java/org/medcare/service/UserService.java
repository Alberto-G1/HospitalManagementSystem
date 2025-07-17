package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.UserDAO;
import org.medcare.models.User;
import org.medcare.service.interfaces.UserServiceInterface;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class UserService implements UserServiceInterface {

    @Inject private UserDAO userDAO;
    @Inject private ActivityLogService activityLogService;

    public UserService() {}

    public UserService(boolean manual) {
        if (manual) {
            this.userDAO = new UserDAO();
        }
    }

    @Override
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find SHA-256 hashing algorithm", e);
        }
    }

    @Override
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    @Override
    public void createUser(User user, String plainPassword) {
        String hashedPassword = hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        userDAO.save(user);
    }

    @Override
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (checkPassword(oldPassword, user.getPassword())) {
            user.setPassword(hashPassword(newPassword));
            userDAO.update(user);
            activityLogService.log("PASSWORD_CHANGE", "User changed their own password.", user);
            return true;
        }
        return false;
    }

    @Override
    public void adminResetPassword(User user, String newPassword, User admin) {
        user.setPassword(hashPassword(newPassword));
        userDAO.update(user);
        activityLogService.log("PASSWORD_RESET", "Admin reset password for user: " + user.getUsername(), admin);
    }

    @Override
    public void updateUser(User user) {
        userDAO.update(user);
    }

    @Override
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

    @Override
    public void softDelete(User user) {
        if (user != null && !"admin".equalsIgnoreCase(user.getUsername())) {
            userDAO.updateUserActiveStatus(user.getUserId(), false);
        }
    }

    @Override
    public void reactivateUser(User user) {
        if (user != null) {
            userDAO.updateUserActiveStatus(user.getUserId(), true);
        }
    }

    @Override
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public User findByUsernameIncludeInactive(String username) {
        return userDAO.findByUsernameIncludeInactive(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public User findByEmailIncludeInactive(String email) {
        return userDAO.findByEmailIncludeInactive(email);
    }

    @Override
    public List<User> getAll() {
        return userDAO.findAll();
    }

    @Override
    public List<User> getAllIncludeInactive() {
        return userDAO.findAllIncludeInactive();
    }

    @Override
    public void saveOrUpdate(User user) {
        userDAO.saveOrUpdate(user);
    }

    @Override
    public User findById(int id) {
        return userDAO.findById(id);
    }
}
