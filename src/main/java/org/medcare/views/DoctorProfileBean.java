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

    @Inject
    private DoctorService doctorService;

    @Inject
    private UserBean userBean; // Get the currently logged-in user

    private Doctor currentDoctorProfile;

    @PostConstruct
    public void init() {
        if (userBean != null && userBean.isLoggedIn()) {
            User currentUser = userBean.getUser();
            // Find the doctor profile associated with the logged-in user
            this.currentDoctorProfile = doctorService.findByUserId(currentUser.getUserId());

            // If no profile exists yet, create a new one and link it to the user
            if (this.currentDoctorProfile == null) {
                this.currentDoctorProfile = new Doctor();
                this.currentDoctorProfile.setUser(currentUser);
                // Pre-populate with email from user account
                this.currentDoctorProfile.setFirstName(""); // Or some default
                this.currentDoctorProfile.setLastName("");
            }
        }
    }

    public void updateProfile() {
        if (currentDoctorProfile != null) {
            try {
                doctorService.save(currentDoctorProfile);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your profile has been updated.");
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving your profile.");
                e.printStackTrace();
            }
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getter and Setter ---
    public Doctor getCurrentDoctorProfile() {
        return currentDoctorProfile;
    }

    public void setCurrentDoctorProfile(Doctor currentDoctorProfile) {
        this.currentDoctorProfile = currentDoctorProfile;
    }
}