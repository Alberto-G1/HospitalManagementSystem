package org.medcare.views;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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

        if (foundUser != null) {
            this.user = foundUser;
            loggedIn = true;
            addMessage(FacesMessage.SEVERITY_INFO, "Login Successful", "Welcome, " + user.getUsername());
            return "/app/dashboard.xhtml?faces-redirect=true";
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password.");
            return null; // Stay on the login page
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/welcome.xhtml?faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters & setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPasswordInput() { return passwordInput; }
    public void setPasswordInput(String passwordInput) { this.passwordInput = passwordInput; }
    public boolean isLoggedIn() { return loggedIn; }
    public String getUsernameInput() { return usernameInput; }
    public void setUsernameInput(String usernameInput) { this.usernameInput = usernameInput; }
}