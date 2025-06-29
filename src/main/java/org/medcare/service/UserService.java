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

    // Manual instantiation for use in ApplicationInitializer
    public UserService() {
        this.userDAO = new UserDAO();
    }

    @Inject
    private UserDAO userDAO;

    /**
     * Hashes a plain-text password using the SHA-256 algorithm.
     * @param password The plain-text password to hash.
     * @return A Base64-encoded string representing the hashed password.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find SHA-256 hashing algorithm", e);
        }
    }

    /**
     * Checks if a given plain-text password matches a stored hashed password.
     * @param plainPassword The password from user input.
     * @param hashedPassword The hash stored in the database.
     * @return true if the passwords match, false otherwise.
     */
    private boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String hashedPlainPassword = hashPassword(plainPassword);
        return hashedPlainPassword.equals(hashedPassword);
    }

    /**
     * Creates a new user, hashes their password, and saves them to the database.
     * @param user The user object with username, email, and role set.
     * @param plainPassword The desired plain-text password for the user.
     */
    public void createUser(User user, String plainPassword) {
        String hashedPassword = hashPassword(plainPassword);
        user.setPassword(hashedPassword);
        userDAO.saveOrUpdate(user);
    }

    /**
     * Authenticates a user by username and plain-text password.
     * @param username The username to look up.
     * @param plainPassword The plain-text password to verify.
     * @return The User object if authentication is successful, otherwise null.
     */
    public User authenticate(String username, String plainPassword) {
        User found = findByUsername(username);
        if (found != null && checkPassword(plainPassword, found.getPassword())) {
            return found;
        }
        return null;
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public List<User> getAll() {
        return userDAO.findAll();
    }

    public void saveOrUpdate(User user) {
        userDAO.saveOrUpdate(user);
    }

    public void delete(User user) {
        // Business rule: The main 'admin' account cannot be deleted.
        if (user != null && "admin".equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("The primary admin account cannot be deleted.");
        }
        if (user != null) {
            userDAO.delete(user);
        }
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }
}