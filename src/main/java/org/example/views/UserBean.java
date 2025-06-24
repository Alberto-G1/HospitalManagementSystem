package org.example.views;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.example.enums.Role;
import org.example.service.UserService;
import org.example.models.User;

import java.io.Serializable;

@Named
@SessionScoped
public class UserBean implements Serializable {
    private User user = new User();
    private final UserService userService = new UserService();

    private boolean loggedIn = false;

    public String register() {
        userService.registerUser(user);
        return "login.xhtml?faces-redirect=true";
    }

    public String login(String passwordInput) {
        User found = userService.findByUsername(user.getUsername());
        if (found != null && found.getPassword().equals(Integer.toHexString(passwordInput.hashCode()))) {
            this.user = found;
            loggedIn = true;
            return "index.xhtml?faces-redirect=true";
        }
        return null;
    }

    public String logout() {
        loggedIn = false;
        user = new User();
        return "login.xhtml?faces-redirect=true";
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public boolean isLoggedIn() { return loggedIn; }
}
