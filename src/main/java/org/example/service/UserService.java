package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.example.dao.UserDAO;
import org.example.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Named
@ApplicationScoped
public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public void registerUser(User user) {
        // Hash password before saving
        user.setPassword(hashPassword(user.getPassword()));
        userDAO.save(user);
    }

    public void updateUser(User user) {
        // If password is provided, hash it
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            user.setPassword(hashPassword(user.getPassword()));
        }
        userDAO.save(user);
    }

    public void deleteUser(User user) {
        userDAO.delete(user);
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    public void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }
}