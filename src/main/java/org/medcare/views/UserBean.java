package org.medcare.views;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.User;
import org.medcare.service.UserService;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Named
@SessionScoped
public class UserBean implements Serializable {

    private User user = new User();
    private String username;
    private String password;
    private String confirmPassword;
    private String passwordInput; // For forms where user.getPassword() is separate
    private boolean loggedIn = false;

    @Inject
    private UserService userService;

    // LOGIN
    public String login() {
        try {
            System.out.println("Login attempt for username: " + (username != null ? username : user.getUsername()));

            // Try to find user by username (from username field or user.username)
            String loginUsername = (username != null && !username.isEmpty()) ? username : user.getUsername();
            User foundUser = userService.findByUsername(loginUsername);

            if (foundUser != null) {
                System.out.println("User found: " + foundUser.getUsername() + ", Role: '" + foundUser.getRole() + "'");

                if (verifyPassword(password != null ? password : passwordInput, foundUser.getPassword())) {
                    this.user = foundUser;
                    this.loggedIn = true;

                    // Clean role
                    if (this.user.getRole() != null) {
                        this.user.setRole(this.user.getRole().trim().toUpperCase());
                    }

                    clearLoginForm();

                    // Redirect by role
                    String role = this.user.getRole();
                    switch (role) {
                        case "ADMIN":
                            return "admin-dashboard.xhtml?faces-redirect=true";
                        case "DOCTOR":
                            return "doctor-dashboard.xhtml?faces-redirect=true";
                        case "RECEPTIONIST":
                            return "receptionist-dashboard.xhtml?faces-redirect=true";
                        default:
                            addErrorMessage("Login Failed", "Unknown role: " + role);
                            return null;
                    }

                } else {
                    addErrorMessage("Login Failed", "Invalid username or password.");
                    return null;
                }

            } else {
                addErrorMessage("Login Failed", "Invalid username or password.");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Login Error", "An error occurred during login: " + e.getMessage());
            return null;
        }
    }

    // REGISTER
    public String register() {
        try {
            if (password != null && !password.equals(confirmPassword)) {
                addErrorMessage("Registration Failed", "Passwords do not match.");
                return null;
            }

            // Check for duplicate username
            if (userService.findByUsername(username) != null) {
                addErrorMessage("Registration Failed", "Username already exists.");
                return null;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(hashPassword(password));
            newUser.setRole(user.getRole() != null ? user.getRole().trim().toUpperCase() : null);

            userService.registerUser(newUser);

            addSuccessMessage("Registration Successful", "User registered successfully! Please login.");
            clearLoginForm();
            return "login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            addErrorMessage("Registration Failed", "Error registering user: " + e.getMessage());
            return null;
        }
    }

    // LOGOUT
    public String logout() {
        System.out.println("Logging out user: " + (user != null ? user.getUsername() : "null"));
        this.user = new User();
        this.loggedIn = false;
        clearLoginForm();

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        addSuccessMessage("Logout", "You have been logged out successfully.");
        return "welcome.xhtml?faces-redirect=true";
    }

    // ROLES / PERMISSIONS
    public boolean isAdmin() {
        return loggedIn && user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public boolean isDoctor() {
        return loggedIn && user != null && "DOCTOR".equalsIgnoreCase(user.getRole());
    }

    public boolean isReceptionist() {
        return loggedIn && user != null && "RECEPTIONIST".equalsIgnoreCase(user.getRole());
    }

    public boolean canManagePatients() {
        return isAdmin() || isReceptionist();
    }

    public boolean canManageDoctors() {
        return isAdmin();
    }

    public boolean canManageAppointments() {
        return isAdmin() || isReceptionist() || isDoctor();
    }

    public boolean canManageUsers() {
        return isAdmin();
    }

    public boolean canViewReports() {
        return isAdmin() || isDoctor();
    }

    public boolean getCanManageDoctors() { return canManageDoctors(); }
    public boolean getCanManagePatients() { return canManagePatients(); }
    public boolean getCanManageAppointments() { return canManageAppointments(); }
    public boolean getCanManageUsers() { return canManageUsers(); }
    public boolean getCanViewReports() { return canViewReports(); }

    // UTILITIES
    private boolean verifyPassword(String inputPassword, String storedHashedPassword) {
        return hashPassword(inputPassword).equals(storedHashedPassword);
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

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    private void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void clearLoginForm() {
        this.username = "";
        this.password = "";
        this.confirmPassword = "";
        this.passwordInput = "";
    }

    // GETTERS / SETTERS
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getPasswordInput() { return passwordInput; }
    public void setPasswordInput(String passwordInput) { this.passwordInput = passwordInput; }

    public boolean isLoggedIn() { return loggedIn; }
    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }

    // Convenience for JSF
    public boolean getAdmin() { return isAdmin(); }
    public boolean getDoctor() { return isDoctor(); }
    public boolean getReceptionist() { return isReceptionist(); }
}
