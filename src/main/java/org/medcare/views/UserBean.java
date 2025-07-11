package org.medcare.views;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Role; // Make sure this import is present
import org.medcare.models.User;
import org.medcare.service.UserService;
import java.io.Serializable;

@Named
@SessionScoped
public class UserBean implements Serializable {
    private String usernameInput;
    private String passwordInput;
    private User user; // Holds the logged-in user's data
    private boolean loggedIn = false;

    @Inject
    private UserService userService;

    public String login() {
        User foundUser = userService.authenticate(usernameInput, passwordInput);

        if (foundUser != null && foundUser.isActive()) { // Also check if the user is active
            this.user = foundUser;
            loggedIn = true;
            addMessage(FacesMessage.SEVERITY_INFO, "Login Successful", "Welcome, " + user.getUsername());
            return "/app/dashboard.xhtml?faces-redirect=true";
        } else {
            if (foundUser != null && !foundUser.isActive()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Your account has been deactivated.");
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password.");
            }
            return null; // Stay on the login page
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // ===================================================
    // === NEW HELPER METHODS FOR ROLE CHECKING ===
    // ===================================================

    /**
     * Checks if the logged-in user has the ADMIN role.
     * This is null-safe and much cleaner to use in XHTML pages.
     * @return true if the user is an Admin, false otherwise.
     */
    public boolean isAdmin() {
        return user != null && user.getRole() == Role.ADMIN;
    }

    /**
     * Checks if the logged-in user has the DOCTOR role.
     * @return true if the user is a Doctor, false otherwise.
     */
    public boolean isDoctor() {
        return user != null && user.getRole() == Role.DOCTOR;
    }

    /**
     * Checks if the logged-in user has the RECEPTIONIST role.
     * @return true if the user is a Receptionist, false otherwise.
     */
    public boolean isReceptionist() {
        return user != null && user.getRole() == Role.RECEPTIONIST;
    }

    /**
     * Checks if the user is either an Admin or a Receptionist.
     * Useful for pages/components visible to both but not to Doctors.
     * @return true if user is Admin or Receptionist, false otherwise.
     */
    public boolean isAdminOrReceptionist() {
        return isAdmin() || isReceptionist();
    }


    // ===================================================
    // === EXISTING GETTERS & SETTERS ===
    // ===================================================
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPasswordInput() { return passwordInput; }
    public void setPasswordInput(String passwordInput) { this.passwordInput = passwordInput; }
    public boolean isLoggedIn() { return loggedIn; }
    public String getUsernameInput() { return usernameInput; }
    public void setUsernameInput(String usernameInput) { this.usernameInput = usernameInput; }
}