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
import java.util.stream.Collectors;

@Named
@ViewScoped
public class StaffManagementBean implements Serializable {

    @Inject private UserService userService;
    @Inject private DoctorService doctorService;
    @Inject private ReceptionistService receptionistService;
    @Inject private UserBean userBean;

    private List<Doctor> activeDoctors;
    private List<Receptionist> activeReceptionists;
    private List<Doctor> inactiveDoctors;
    private List<Receptionist> inactiveReceptionists;

    private Doctor selectedDoctor;
    private Receptionist selectedReceptionist;

    // This will now be the single source of truth for all operations
    private User userForOperation;

    private String initialPassword;
    private String newPasswordForReset;

    @PostConstruct
    public void init() {
        loadStaffLists();
        userForOperation = new User();
    }

    private void loadStaffLists() {
        List<Doctor> allDoctors = doctorService.getAllIncludeInactive();
        activeDoctors = allDoctors.stream().filter(d -> d.getUser().isActive()).collect(Collectors.toList());
        inactiveDoctors = allDoctors.stream().filter(d -> !d.getUser().isActive()).collect(Collectors.toList());

        List<Receptionist> allReceptionists = receptionistService.getAllIncludeInactive();
        activeReceptionists = allReceptionists.stream().filter(r -> r.getUser().isActive()).collect(Collectors.toList());
        inactiveReceptionists = allReceptionists.stream().filter(r -> !r.getUser().isActive()).collect(Collectors.toList());
    }

    public void openNewDoctor() {
        selectedDoctor = new Doctor();
        userForOperation = new User();
        userForOperation.setRole(Role.DOCTOR);
        initialPassword = generateRandomPassword();
    }

    public void openNewReceptionist() {
        selectedReceptionist = new Receptionist();
        userForOperation = new User();
        userForOperation.setRole(Role.RECEPTIONIST);
        initialPassword = generateRandomPassword();
    }

    public void saveDoctor() {
        // ... (this logic is fine, but let's make it safer)
        if (isUsernameOrEmailTaken(userForOperation, userForOperation.getUsername(), userForOperation.getEmail())) {
            return;
        }
        try {
            boolean isNew = selectedDoctor.getDoctorId() == 0;
            if (isNew) {
                userForOperation.setActive(true);
                userService.createUser(userForOperation, initialPassword);
                User createdUser = userService.findByUsernameIncludeInactive(userForOperation.getUsername());
                selectedDoctor.setUser(createdUser);
            } else {
                // When editing, userForOperation is already the correct user object
                userService.updateUser(userForOperation);
                selectedDoctor.setUser(userForOperation);
            }
            doctorService.saveOrUpdate(selectedDoctor, userBean.getUser());
            loadStaffLists();
            addMessage(FacesMessage.SEVERITY_INFO, "Doctor Saved", selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName() + " was saved.");
            PrimeFaces.current().executeScript("PF('manageDoctorDialog').hide()");
            PrimeFaces.current().ajax().update("staffForm:messages", "staffForm:staffTabView");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save doctor: " + e.getMessage());
        }
    }

    public void saveReceptionist() {
        // ... (this logic is fine, but let's make it safer)
        if (isUsernameOrEmailTaken(userForOperation, userForOperation.getUsername(), userForOperation.getEmail())) {
            return;
        }
        try {
            boolean isNew = selectedReceptionist.getReceptionistId() == 0;
            if (isNew) {
                userForOperation.setActive(true);
                userService.createUser(userForOperation, initialPassword);
                User createdUser = userService.findByUsernameIncludeInactive(userForOperation.getUsername());
                selectedReceptionist.setUser(createdUser);
            } else {
                userService.updateUser(userForOperation);
                selectedReceptionist.setUser(userForOperation);
            }
            receptionistService.saveOrUpdate(selectedReceptionist, userBean.getUser());
            loadStaffLists();
            addMessage(FacesMessage.SEVERITY_INFO, "Receptionist Saved", "Receptionist saved successfully.");
            PrimeFaces.current().executeScript("PF('manageReceptionistDialog').hide()");
            PrimeFaces.current().ajax().update("staffForm:messages", "staffForm:staffTabView");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save receptionist: " + e.getMessage());
        }
    }

    public void deactivateDoctor() {
        if (selectedDoctor != null) {
            doctorService.softDelete(selectedDoctor, userBean.getUser());
            loadStaffLists();
            addMessage(FacesMessage.SEVERITY_WARN, "Doctor Deactivated", "The doctor's account has been deactivated.");
        }
    }

    public void deactivateReceptionist() {
        if (selectedReceptionist != null) {
            receptionistService.softDelete(selectedReceptionist, userBean.getUser());
            loadStaffLists();
            addMessage(FacesMessage.SEVERITY_WARN, "Receptionist Deactivated", "Account deactivated.");
        }
    }

    public void reactivateDoctor(Doctor doctor) {
        doctorService.reactivate(doctor, userBean.getUser());
        loadStaffLists();
        addMessage(FacesMessage.SEVERITY_INFO, "Doctor Reactivated", doctor.getFirstName() + " has been reactivated.");
    }

    public void reactivateReceptionist(Receptionist receptionist) {
        receptionistService.reactivate(receptionist, userBean.getUser());
        loadStaffLists();
        addMessage(FacesMessage.SEVERITY_INFO, "Receptionist Reactivated", receptionist.getFirstName() + " has been reactivated.");
    }

    /**
     * Prepares the password reset dialog by setting the user object and generating a new password.
     * This method is now safe and doesn't rely on selectedDoctor/Receptionist.
     */
    public void preparePasswordReset(User user) {
        this.userForOperation = user;
        this.newPasswordForReset = generateRandomPassword();
    }

    /**
     * Executes the password reset using the 'userForOperation' object.
     * This method is now safe and free from NullPointerExceptions.
     */
    public void executePasswordReset() {
        if (userForOperation != null) {
            userService.adminResetPassword(userForOperation, newPasswordForReset, userBean.getUser());
            addMessage(FacesMessage.SEVERITY_INFO, "Password Reset", "Password for " + userForOperation.getUsername() + " has been reset.");
            PrimeFaces.current().executeScript("PF('resetPasswordDialog').hide()");
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No user was selected for password reset.");
        }
    }

    private boolean isUsernameOrEmailTaken(User editingUser, String username, String email) {
        User userByUsername = userService.findByUsernameIncludeInactive(username);
        if (userByUsername != null && (editingUser.getUserId() == 0 || editingUser.getUserId() != userByUsername.getUserId())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Username is already taken.");
            return true;
        }
        User userByEmail = userService.findByEmailIncludeInactive(email);
        if (userByEmail != null && (editingUser.getUserId() == 0 || editingUser.getUserId() != userByEmail.getUserId())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Email is already registered.");
            return true;
        }
        return false;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters and Setters ---
    // Updated setters to also set the 'userForOperation'
    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(Doctor selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
        this.userForOperation = (selectedDoctor != null && selectedDoctor.getUser() != null) ? selectedDoctor.getUser() : new User();
        this.initialPassword = null;
    }
    public Receptionist getSelectedReceptionist() { return selectedReceptionist; }
    public void setSelectedReceptionist(Receptionist selectedReceptionist) {
        this.selectedReceptionist = selectedReceptionist;
        this.userForOperation = (selectedReceptionist != null && selectedReceptionist.getUser() != null) ? selectedReceptionist.getUser() : new User();
        this.initialPassword = null;
    }

    // Changed 'getNewUser' to 'getUserForOperation' for clarity
    public User getUserForOperation() { return userForOperation; }
    public void setUserForOperation(User userForOperation) { this.userForOperation = userForOperation; }

    public String getInitialPassword() { return initialPassword; }
    public String getNewPasswordForReset() { return newPasswordForReset; }

    // Other getters...
    public List<Doctor> getActiveDoctors() { return activeDoctors; }
    public List<Receptionist> getActiveReceptionists() { return activeReceptionists; }
    public List<Doctor> getInactiveDoctors() { return inactiveDoctors; }
    public List<Receptionist> getInactiveReceptionists() { return inactiveReceptionists; }
}