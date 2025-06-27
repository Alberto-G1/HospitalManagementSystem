package org.medcare.views;


import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.User;
import org.medcare.service.UserService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SystemUserBean implements Serializable {

    private User user = new User();
    private List<User> users;
    private boolean editMode = false;

    @Inject
    private UserService userService;

    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    public void loadUsers() {
        users = userService.getAllUsers();
    }

    public String addUser() {
        try {
            userService.registerUser(user);
            userService.addSuccessMessage("Success", "User added successfully!");
            resetForm();
            loadUsers();
            return "system-users.xhtml?faces-redirect=true";
        } catch (Exception e) {
            userService.addErrorMessage("Add Failed", "Error adding user: " + e.getMessage());
            return null;
        }
    }

    public String updateUser() {
        try {
            userService.updateUser(user);
            userService.addSuccessMessage("Success", "User updated successfully!");
            resetForm();
            loadUsers();
            editMode = false;
            return "system-users.xhtml?faces-redirect=true";
        } catch (Exception e) {
            userService.addErrorMessage("Update Failed", "Error updating user: " + e.getMessage());
            return null;
        }
    }

    public void editUser(User u) {
        this.user = new User();
//        this.user.setUserID(u.getUserID());
        this.user.setUsername(u.getUsername());
        this.user.setEmail(u.getEmail());
        this.user.setRole(u.getRole());
        // Don't copy password for security
        this.editMode = true;
    }

    public String deleteUser(int userId) {
        try {
            User u = userService.findById(userId);
            if (u != null) {
                userService.deleteUser(u);
                userService.addSuccessMessage("Success", "User deleted successfully!");
                loadUsers();
            }
            return "system-users.xhtml?faces-redirect=true";
        } catch (Exception e) {
            userService.addErrorMessage("Delete Failed", "Error deleting user: " + e.getMessage());
            return null;
        }
    }

    public void cancelEdit() {
        resetForm();
        editMode = false;
    }

    private void resetForm() {
        user = new User();
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<User> getUsers() { return users; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
}