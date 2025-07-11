package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Receptionist;
import org.medcare.service.ReceptionistService;
import java.io.Serializable;

@Named
@ViewScoped
public class ReceptionistProfileBean implements Serializable {

    @Inject private ReceptionistService receptionistService;
    @Inject private UserBean userBean;

    private Receptionist currentProfile;
    private boolean editMode = false; // Start in "view" mode by default

    @PostConstruct
    public void init() {
        loadProfile();
    }

    /**
     * Loads the current user's receptionist profile from the database.
     */
    private void loadProfile() {
        if (userBean != null && userBean.getUser() != null) {
            currentProfile = receptionistService.findByUserId(userBean.getUser().getUserId());
        }
    }

    /**
     * Saves the changes made to the profile.
     */
    public void updateProfile() {
        try {
            receptionistService.saveOrUpdate(currentProfile, userBean.getUser());
            this.editMode = false; // Switch back to view mode after saving
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your profile has been updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update profile: " + e.getMessage());
        }
    }

    /**
     * Switches the UI to edit mode.
     */
    public void enableEditMode() {
        this.editMode = true;
    }

    /**
     * Cancels the edit, discards changes, and returns to view mode.
     */
    public void cancelEdit() {
        this.editMode = false;
        loadProfile(); // Reload original data from the database to discard any changes
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters and Setters ---
    public Receptionist getCurrentProfile() { return currentProfile; }
    public void setCurrentProfile(Receptionist currentProfile) { this.currentProfile = currentProfile; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
}