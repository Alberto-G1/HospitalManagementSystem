package org.medcare.views;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.User;
import org.medcare.service.UserService;
import org.medcare.utils.PasswordUtil;

import java.io.Serializable;

@Named
@SessionScoped
public class UserBean implements Serializable {
    private User user = new User();
    private String passwordInput; // For form binding
    private boolean loggedIn = false;

    @Inject
    private UserService userService;

    public String register() {
        // 1. Hash the password before saving
        String hashedPassword = PasswordUtil.hashPassword(passwordInput);
        user.setPassword(hashedPassword);

        // 2. Save the user
        userService.register(user);

        // 3. Provide feedback and reset the form
        addMessage(FacesMessage.SEVERITY_INFO, "Registration Successful", "Please log in with your new account.");
        user = new User();
        passwordInput = "";
        return "login.xhtml?faces-redirect=true";
    }

    public String login() {
        User found = userService.findByUsername(user.getUsername());

        // Use the utility to check the password
        if (found != null && PasswordUtil.checkPassword(passwordInput, found.getPassword())) {
            this.user = found;
            loggedIn = true;
            // Redirect to the dashboard inside the secure /app/ folder
            return "/app/dashboard.xhtml?faces-redirect=true";
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password.");
            return null; // Stay on the login page
        }
    }

    public String logout() {
        // Invalidate the session to clear all data
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        // Redirect to the public welcome page
        return "/welcome.xhtml?faces-redirect=true";
    }

    // Helper method for showing messages
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }


    // Getters & setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPasswordInput() { return passwordInput; }
    public void setPasswordInput(String passwordInput) { this.passwordInput = passwordInput; }
    public boolean isLoggedIn() { return loggedIn; }
}