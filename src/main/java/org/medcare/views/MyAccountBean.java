package org.medcare.views;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.User;
import org.medcare.service.UserService;

import java.io.Serializable;

@Named
@ViewScoped
public class MyAccountBean implements Serializable {

    @Inject private UserService userService;
    @Inject private UserBean userBean;

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public void changePassword() {
        if (!newPassword.equals(confirmPassword)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "New passwords do not match.");
            return;
        }

        User currentUser = userBean.getUser();
        boolean success = userService.changePassword(currentUser, currentPassword, newPassword);

        if (success) {
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your password has been changed successfully.");
            // Clear fields
            currentPassword = null;
            newPassword = null;
            confirmPassword = null;
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "The current password you entered is incorrect.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
