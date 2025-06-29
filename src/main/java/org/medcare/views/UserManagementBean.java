package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Role;
import org.medcare.models.User;
import org.medcare.service.UserService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class UserManagementBean implements Serializable {

    @Inject
    private UserService userService;

    private List<User> users;
    private User selectedUser;
    private String initialPassword; // For creating a new user

    @PostConstruct
    public void init() {
        // Exclude the currently logged-in admin from the list to prevent self-modification
        users = userService.getAll();
    }

    public void openNew() {
        this.selectedUser = new User();
        this.initialPassword = generateRandomPassword(); // Suggest a strong password
    }

    public void saveUser() {
        // Validate unique username and email
        if (isUsernameTaken(selectedUser.getUsername()) || isEmailTaken(selectedUser.getEmail())) {
            return; // Messages are added within the validation methods
        }

        try {
            userService.createUser(selectedUser, initialPassword);
            users = userService.getAll(); // Refresh list

            // Show success message with the generated password
            String summary = "User Created Successfully";
            String detail = "User '" + selectedUser.getUsername() + "' created. Initial password: " + initialPassword;
            addMessage(FacesMessage.SEVERITY_INFO, summary, detail);

            PrimeFaces.current().executeScript("PF('manageUserDialog').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:dt-users");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not create user: " + e.getMessage());
        }
    }

    public void deleteUser() {
        if (selectedUser != null && !"admin".equalsIgnoreCase(selectedUser.getUsername())) {
            try {
                userService.delete(selectedUser);
                users.remove(selectedUser);
                selectedUser = null;
                addMessage(FacesMessage.SEVERITY_WARN, "User Deleted", "The selected user has been removed.");
                PrimeFaces.current().ajax().update("form:messages", "form:dt-users");
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete user: " + e.getMessage());
            }
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Action Denied", "The primary admin account cannot be deleted.");
        }
    }

    private boolean isUsernameTaken(String username) {
        if (userService.findByUsername(username) != null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Username '" + username + "' is already taken.");
            return true;
        }
        return false;
    }

    private boolean isEmailTaken(String email) {
        if (userService.findByEmail(email) != null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Email '" + email + "' is already registered.");
            return true;
        }
        return false;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Role[] getRoles() {
        // Admins can create other Admins, Doctors, or Receptionists
        return Role.values();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters and Setters ---
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public User getSelectedUser() { return selectedUser; }
    public void setSelectedUser(User selectedUser) { this.selectedUser = selectedUser; }
    public String getInitialPassword() { return initialPassword; }
    public void setInitialPassword(String initialPassword) { this.initialPassword = initialPassword; }
}