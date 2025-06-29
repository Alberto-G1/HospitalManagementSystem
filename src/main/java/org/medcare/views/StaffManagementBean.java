package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Role;
import org.medcare.models.Doctor;
import org.medcare.models.Receptionist;
import org.medcare.models.User;
import org.medcare.service.DoctorService;
import org.medcare.service.ReceptionistService;
import org.medcare.service.UserService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class StaffManagementBean implements Serializable {

    @Inject private UserService userService;
    @Inject private DoctorService doctorService;
    @Inject private ReceptionistService receptionistService;

    private List<Doctor> doctors;
    private List<Receptionist> receptionists;

    private Doctor selectedDoctor;
    private Receptionist selectedReceptionist;

    private User newUser;
    private String initialPassword;

    @PostConstruct
    public void init() {
        doctors = doctorService.getAll();
        receptionists = receptionistService.getAll();
        // Initialize newUser to prevent NullPointerException in dialogs before 'openNew'
        newUser = new User();
    }

    public void openNewDoctor() {
        selectedDoctor = new Doctor();
        newUser = new User();
        newUser.setRole(Role.DOCTOR);
        initialPassword = generateRandomPassword();
    }

    public void openNewReceptionist() {
        selectedReceptionist = new Receptionist();
        newUser = new User();
        newUser.setRole(Role.RECEPTIONIST);
        initialPassword = generateRandomPassword();
    }

    public void saveDoctor() {
        if (isUsernameOrEmailTaken(newUser.getUsername(), newUser.getEmail())) return;

        try {
            // Set user to active by default
            newUser.setActive(true);
            userService.createUser(newUser, initialPassword);

            // It's better to refetch the user to ensure it has the generated ID
            User createdUser = userService.findByUsernameIncludeInactive(newUser.getUsername());

            selectedDoctor.setUser(createdUser);
            doctorService.save(selectedDoctor);

            doctors = doctorService.getAll(); // Refresh list
            addMessage(FacesMessage.SEVERITY_INFO, "Doctor Created", "Username: " + newUser.getUsername() + " | Password: " + initialPassword);
            PrimeFaces.current().executeScript("PF('manageDoctorDialog').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:doctor-tab:dt-doctors");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not create doctor.");
            e.printStackTrace();
        }
    }

    public void saveReceptionist() {
        if (isUsernameOrEmailTaken(newUser.getUsername(), newUser.getEmail())) return;

        try {
            // Set user to active by default
            newUser.setActive(true);
            userService.createUser(newUser, initialPassword);

            User createdUser = userService.findByUsernameIncludeInactive(newUser.getUsername());

            selectedReceptionist.setUser(createdUser);
            receptionistService.save(selectedReceptionist);

            receptionists = receptionistService.getAll(); // Refresh list
            addMessage(FacesMessage.SEVERITY_INFO, "Receptionist Created", "Username: " + newUser.getUsername() + " | Password: " + initialPassword);
            PrimeFaces.current().executeScript("PF('manageReceptionistDialog').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:receptionist-tab:dt-receptionists");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not create receptionist.");
            e.printStackTrace();
        }
    }

    public void deleteDoctor() {
        if (selectedDoctor != null) {
            doctorService.softDelete(selectedDoctor);
            doctors.remove(selectedDoctor);
            selectedDoctor = null; // Deselect
            addMessage(FacesMessage.SEVERITY_WARN, "Doctor Deactivated", "The doctor's account has been deactivated.");
        }
    }

    public void deleteReceptionist() {
        if (selectedReceptionist != null) {
            receptionistService.softDelete(selectedReceptionist);
            receptionists.remove(selectedReceptionist);
            selectedReceptionist = null; // Deselect
            addMessage(FacesMessage.SEVERITY_WARN, "Receptionist Deactivated", "The receptionist's account has been deactivated.");
        }
    }

    private boolean isUsernameOrEmailTaken(String username, String email) {
        boolean taken = false;
        if (userService.findByUsernameIncludeInactive(username) != null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Username is already taken.");
            taken = true;
        }
        if (userService.findByEmail(email) != null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Email is already registered.");
            taken = true;
        }
        return taken;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // ===================================================================
    // GETTERS AND SETTERS - THE MISSING PIECE
    // ===================================================================

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public List<Receptionist> getReceptionists() {
        return receptionists;
    }

    public void setReceptionists(List<Receptionist> receptionists) {
        this.receptionists = receptionists;
    }

    public Doctor getSelectedDoctor() {
        return selectedDoctor;
    }

    public void setSelectedDoctor(Doctor selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
    }

    public Receptionist getSelectedReceptionist() {
        return selectedReceptionist;
    }

    public void setSelectedReceptionist(Receptionist selectedReceptionist) {
        this.selectedReceptionist = selectedReceptionist;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public String getInitialPassword() {
        return initialPassword;
    }

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }
}