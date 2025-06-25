package org.example.views;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.models.User;
import org.example.service.UserService;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Named
@SessionScoped
public class UserBean implements Serializable {

    private User user = new User();
    private String passwordInput;
    private boolean loggedIn = false;

    @Inject
    private UserService userService;

    public String login() {
        try {
            System.out.println("Login attempt for username: " + user.getUsername());
            User foundUser = userService.findByUsername(user.getUsername());

            if (foundUser != null) {
                System.out.println("User found: " + foundUser.getUsername() + ", Role: '" + foundUser.getRole() + "'");

                if (verifyPassword(passwordInput, foundUser.getPassword())) {
                    // Set the complete user object and login status
                    this.user = foundUser;
                    this.loggedIn = true;

                    // Clean up the role field to remove any whitespace
                    if (this.user.getRole() != null) {
                        this.user.setRole(this.user.getRole().trim().toUpperCase());
                    }

                    System.out.println("Login successful. User role after cleanup: '" + this.user.getRole() + "', Logged in: " + this.loggedIn);

                    addSuccessMessage("Login Successful", "Welcome, " + foundUser.getUsername() + "!");

                    // Clear password input for security
                    this.passwordInput = null;

                    // Redirect based on role
                    String role = String.valueOf(this.user.getRole());
                    if ("ADMIN".equals(role)) {
                        System.out.println("Redirecting to admin dashboard");
                        return "admin-dashboard.xhtml?faces-redirect=true";
                    } else if ("DOCTOR".equals(role)) {
                        System.out.println("Redirecting to doctor dashboard");
                        return "doctor-dashboard.xhtml?faces-redirect=true";
                    } else if ("RECEPTIONIST".equals(role)) {
                        System.out.println("Redirecting to receptionist dashboard");
                        return "receptionist-dashboard.xhtml?faces-redirect=true";
                    } else {
                        System.out.println("Unknown role: '" + role + "', redirecting to welcome");
                        return "welcome.xhtml?faces-redirect=true";
                    }
                } else {
                    System.out.println("Password verification failed");
                    addErrorMessage("Login Failed", "Invalid username or password.");
                    return null;
                }
            } else {
                System.out.println("User not found");
                addErrorMessage("Login Failed", "Invalid username or password.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("Login Error", "An error occurred during login: " + e.getMessage());
            return null;
        }
    }

    public String register() {
        try {
            // Check if username already exists
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                addErrorMessage("Registration Failed", "Username already exists.");
                return null;
            }

            // Clean up role before saving
            if (user.getRole() != null) {
                user.setRole(user.getRole().trim().toUpperCase());
            }

            userService.registerUser(user);
            addSuccessMessage("Registration Successful", "User registered successfully! Please login.");

            // Reset form
            user = new User();
            return "login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Registration Failed", "Error registering user: " + e.getMessage());
            return null;
        }
    }

    public String logout() {
        System.out.println("Logging out user: " + (user != null ? user.getUsername() : "null"));
        this.user = new User();
        this.loggedIn = false;
        this.passwordInput = null;

        // Invalidate session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        addSuccessMessage("Logout", "You have been logged out successfully.");
        return "welcome.xhtml?faces-redirect=true";
    }

    private boolean verifyPassword(String inputPassword, String storedPassword) {
        return hashPassword(inputPassword).equals(storedPassword);
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

    private void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    // Role checking methods with better debugging and null safety
    public boolean isAdmin() {
        if (!loggedIn || user == null || user.getRole() == null) {
            System.out.println("isAdmin() check: loggedIn=" + loggedIn + ", user=" + (user != null ? user.getUsername() : "null") + ", role=null, result=false");
            return false;
        }

        String role = user.getRole().trim().toUpperCase();
        boolean result = "ADMIN".equals(role);
        System.out.println("isAdmin() check: loggedIn=" + loggedIn + ", user=" + user.getUsername() + ", role='" + role + "', result=" + result);
        return result;
    }

    public boolean isDoctor() {
        if (!loggedIn || user == null || user.getRole() == null) {
            System.out.println("isDoctor() check: loggedIn=" + loggedIn + ", user=" + (user != null ? user.getUsername() : "null") + ", role=null, result=false");
            return false;
        }

        String role = user.getRole().trim().toUpperCase();
        boolean result = "DOCTOR".equals(role);
        System.out.println("isDoctor() check: loggedIn=" + loggedIn + ", user=" + user.getUsername() + ", role='" + role + "', result=" + result);
        return result;
    }

    public boolean isReceptionist() {
        if (!loggedIn || user == null || user.getRole() == null) {
            System.out.println("isReceptionist() check: loggedIn=" + loggedIn + ", user=" + (user != null ? user.getUsername() : "null") + ", role=null, result=false");
            return false;
        }

        String role = user.getRole().trim().toUpperCase();
        boolean result = "RECEPTIONIST".equals(role);
        System.out.println("isReceptionist() check: loggedIn=" + loggedIn + ", user=" + user.getUsername() + ", role='" + role + "', result=" + result);
        return result;
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

    public boolean canViewReports() {
        return isAdmin() || isDoctor();
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    // Convenience getters for JSF (these are what JSF calls)
    public boolean getAdmin() {
        return isAdmin();
    }

    public boolean getDoctor() {
        return isDoctor();
    }

    public boolean getReceptionist() {
        return isReceptionist();
    }
}