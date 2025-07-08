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
        System.out.println("StaffManagementBean initialized");
        doctors = doctorService.getAll();
        receptionists = receptionistService.getAll();
        selectedDoctor = new Doctor();
        selectedReceptionist = new Receptionist();
        newUser = new User();
    }

    public void openNewDoctor() {
        System.out.println("Opening new doctor dialog");
        selectedDoctor = new Doctor();
        newUser = new User();
        newUser.setRole(Role.DOCTOR);
        initialPassword = generateRandomPassword();
    }

    public void openNewReceptionist() {
        System.out.println("Opening new receptionist dialog");
        selectedReceptionist = new Receptionist();
        newUser = new User();
        newUser.setRole(Role.RECEPTIONIST);
        initialPassword = generateRandomPassword();
    }

    public void editDoctor(Doctor doctor) {
        System.out.println("Editing doctor: " + (doctor != null ? doctor.getFirstName() : "null"));
        selectedDoctor = (doctor != null) ? doctor : new Doctor();
        newUser = (doctor != null && doctor.getUser() != null) ? doctor.getUser() : new User();
        newUser.setRole(Role.DOCTOR);
        initialPassword = null;
    }

    public void editReceptionist(Receptionist receptionist) {
        System.out.println("Editing receptionist: " + (receptionist != null ? receptionist.getFirstName() : "null"));
        selectedReceptionist = (receptionist != null) ? receptionist : new Receptionist();
        newUser = (receptionist != null && receptionist.getUser() != null) ? receptionist.getUser() : new User();
        newUser.setRole(Role.RECEPTIONIST);
        initialPassword = null;
    }

    public void saveDoctor() {
        System.out.println("Saving doctor: " + (selectedDoctor != null ? selectedDoctor.getFirstName() : "null"));
        if (isUsernameOrEmailTaken(newUser.getUsername(), newUser.getEmail())) {
            return;
        }

        try {
            if (selectedDoctor.getDoctorId() == 0) { // New doctor
                newUser.setActive(true);
                userService.createUser(newUser, initialPassword);
                User createdUser = userService.findByUsernameIncludeInactive(newUser.getUsername());
                selectedDoctor.setUser(createdUser);
            } else { // Existing doctor
                userService.update(newUser);
                selectedDoctor.setUser(newUser);
            }
            doctorService.save(selectedDoctor);
            doctors = doctorService.getAll();
            selectedDoctor = new Doctor();
            newUser = new User();
            initialPassword = null;
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Doctor Saved",
                    "Doctor saved successfully. Username: " + newUser.getUsername());
            PrimeFaces.current().executeScript("PF('manageDoctorDialog').hide()");
            PrimeFaces.current().ajax().update("staffForm:messages", "staffForm:staffTabView:dt-doctors");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save doctor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveReceptionist() {
        System.out.println("Saving receptionist: " + (selectedReceptionist != null ? selectedReceptionist.getFirstName() : "null"));
        if (isUsernameOrEmailTaken(newUser.getUsername(), newUser.getEmail())) {
            return;
        }

        try {
            if (selectedReceptionist.getReceptionistId() == 0) { // New receptionist
                newUser.setActive(true);
                userService.createUser(newUser, initialPassword);
                User createdUser = userService.findByUsernameIncludeInactive(newUser.getUsername());
                selectedReceptionist.setUser(createdUser);
            } else { // Existing receptionist
                userService.update(newUser);
                selectedReceptionist.setUser(newUser);
            }
            receptionistService.save(selectedReceptionist);
            receptionists = receptionistService.getAll();
            selectedReceptionist = new Receptionist();
            newUser = new User();
            initialPassword = null;
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Receptionist Saved",
                    "Receptionist saved successfully. Username: " + newUser.getUsername());
            PrimeFaces.current().executeScript("PF('manageReceptionistDialog').hide()");
            PrimeFaces.current().ajax().update("staffForm:messages", "staffForm:staffTabView:dt-receptionists");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save receptionist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteDoctor() {
        if (selectedDoctor != null && selectedDoctor.getDoctorId() != 0) {
            try {
                System.out.println("Deactivating doctor: " + selectedDoctor.getFirstName());
                doctorService.softDelete(selectedDoctor);
                doctors = doctorService.getAll();
                selectedDoctor = new Doctor();
                newUser = new User();
                addMessage(FacesMessage.SEVERITY_WARN, "Doctor Deactivated", "The doctor's account has been deactivated.");
                PrimeFaces.current().ajax().update("staffForm:staffTabView:dt-doctors", "staffForm:messages");
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not deactivate doctor: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No doctor selected for deletion.");
        }
    }

    public void deleteReceptionist() {
        if (selectedReceptionist != null && selectedReceptionist.getReceptionistId() != 0) {
            try {
                System.out.println("Deactivating receptionist: " + selectedReceptionist.getFirstName());
                receptionistService.softDelete(selectedReceptionist);
                receptionists = receptionistService.getAll();
                selectedReceptionist = new Receptionist();
                newUser = new User();
                addMessage(FacesMessage.SEVERITY_WARN, "Receptionist Deactivated", "The receptionist's account has been deactivated.");
                PrimeFaces.current().ajax().update("staffForm:staffTabView:dt-receptionists", "staffForm:messages");
            } catch (Exception e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not deactivate receptionist: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No receptionist selected for deletion.");
        }
    }

    private boolean isUsernameOrEmailTaken(String username, String email) {
        boolean taken = false;
        if (userService.findByUsernameIncludeInactive(username) != null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Username is already taken.");
            taken = true;
        }
        if (userService.findByEmailIncludeInactive(email) != null) {
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

    // Getters and Setters
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
        if (selectedDoctor == null) {
            System.out.println("getSelectedDoctor: Initializing new Doctor due to null");
            selectedDoctor = new Doctor();
        }
        System.out.println("Getting selectedDoctor: " + (selectedDoctor.getFirstName() != null ? selectedDoctor.getFirstName() : "empty"));
        return selectedDoctor;
    }

    public void setSelectedDoctor(Doctor selectedDoctor) {
        if (selectedDoctor == null) {
            System.out.println("Warning: Attempted to set selectedDoctor to null");
            this.selectedDoctor = new Doctor();
        } else {
            this.selectedDoctor = selectedDoctor;
            System.out.println("setSelectedDoctor: Set to " + (selectedDoctor.getFirstName() != null ? selectedDoctor.getFirstName() : "empty"));
        }
    }

    public Receptionist getSelectedReceptionist() {
        if (selectedReceptionist == null) {
            System.out.println("getSelectedReceptionist: Initializing new Receptionist due to null");
            selectedReceptionist = new Receptionist();
        }
        System.out.println("Getting selectedReceptionist: " + (selectedReceptionist.getFirstName() != null ? selectedReceptionist.getFirstName() : "empty"));
        return selectedReceptionist;
    }

    public void setSelectedReceptionist(Receptionist selectedReceptionist) {
        if (selectedReceptionist == null) {
            System.out.println("Warning: Attempted to set selectedReceptionist to null");
            this.selectedReceptionist = new Receptionist();
        } else {
            this.selectedReceptionist = selectedReceptionist;
            System.out.println("setSelectedReceptionist: Set to " + (selectedReceptionist.getFirstName() != null ? selectedReceptionist.getFirstName() : "empty"));
        }
    }

    public User getNewUser() {
        if (newUser == null) {
            System.out.println("getNewUser: Initializing new User due to null");
            newUser = new User();
        }
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = (newUser != null) ? newUser : new User();
    }

    public String getInitialPassword() {
        return initialPassword;
    }

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }
}