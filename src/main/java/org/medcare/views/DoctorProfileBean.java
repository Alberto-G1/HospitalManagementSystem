package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Doctor;
import org.medcare.service.DoctorService;
import java.io.Serializable;

@Named
@ViewScoped
public class DoctorProfileBean implements Serializable {

    @Inject private DoctorService doctorService;
    @Inject private UserBean userBean;

    private Doctor currentProfile;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        loadProfile();
    }

    private void loadProfile() {
        if (userBean != null && userBean.getUser() != null) {
            currentProfile = doctorService.findByUserId(userBean.getUser().getUserId());
        }
    }

    public void updateProfile() {
        try {
            doctorService.saveOrUpdate(currentProfile, userBean.getUser());
            this.editMode = false;
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your profile has been updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update profile: " + e.getMessage());
        }
    }

    public void enableEditMode() {
        this.editMode = true;
    }

    public void cancelEdit() {
        this.editMode = false;
        loadProfile();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters and Setters ---
    public Doctor getCurrentProfile() { return currentProfile; }
    public void setCurrentProfile(Doctor currentProfile) { this.currentProfile = currentProfile; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
}