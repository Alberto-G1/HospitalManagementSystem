package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Doctor;
import org.medcare.models.User;
import org.medcare.service.DoctorService;
import java.io.Serializable;

@Named
@ViewScoped
public class DoctorProfileBean implements Serializable {

    @Inject private DoctorService doctorService;
    @Inject private UserBean userBean;

    private Doctor currentDoctorProfile;
    private boolean editMode = false; // Flag to toggle between view and edit modes

    @PostConstruct
    public void init() {
        if (userBean != null && userBean.isLoggedIn()) {
            User currentUser = userBean.getUser();
            // Find the doctor profile associated with the logged-in user
            this.currentDoctorProfile = doctorService.findByUserId(currentUser.getUserId());

            // If no profile exists yet, create one and force edit mode
            if (this.currentDoctorProfile == null) {
                this.currentDoctorProfile = new Doctor();
                this.currentDoctorProfile.setUser(currentUser);
                this.editMode = true; // Force edit mode if profile is new
            }
        }
    }

    public void updateProfile() {
        if (currentDoctorProfile != null) {
            try {
                doctorService.save(currentDoctorProfile);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your profile has been updated.");
                this.editMode = false; // Switch back to view mode after saving
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving your profile.");
                e.printStackTrace();
            }
        }
    }

    public void toggleEditMode() {
        this.editMode = !this.editMode;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters and Setters ---
    public Doctor getCurrentDoctorProfile() {
        return currentDoctorProfile;
    }

    public void setCurrentDoctorProfile(Doctor currentDoctorProfile) {
        this.currentDoctorProfile = currentDoctorProfile;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}