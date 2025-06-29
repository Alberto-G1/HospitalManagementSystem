package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Receptionist;
import org.medcare.models.User;
import org.medcare.service.ReceptionistService;
import java.io.Serializable;

@Named
@ViewScoped
public class ReceptionistProfileBean implements Serializable {

    @Inject private ReceptionistService receptionistService;
    @Inject private UserBean userBean;

    private Receptionist currentProfile;

    @PostConstruct
    public void init() {
        User currentUser = userBean.getUser();
        if (currentUser != null && currentUser.getRole() == org.medcare.enums.Role.RECEPTIONIST) {
            currentProfile = receptionistService.findByUserId(currentUser.getUserId());
            if (currentProfile == null) {
                // This case should ideally not happen if admin creates profile with user
                currentProfile = new Receptionist();
                currentProfile.setUser(currentUser);
            }
        }
    }

    public void updateProfile() {
        try {
            receptionistService.save(currentProfile);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your profile has been updated.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving.");
            e.printStackTrace();
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters & Setters
    public Receptionist getCurrentProfile() { return currentProfile; }
    public void setCurrentProfile(Receptionist currentProfile) { this.currentProfile = currentProfile; }
}