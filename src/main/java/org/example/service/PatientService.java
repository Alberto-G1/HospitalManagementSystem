package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.dao.PatientDAO;
import org.example.models.Patient;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

@Named
@ApplicationScoped
public class PatientService {
    private final PatientDAO patientDAO = new PatientDAO();

    @Inject
    private ActivityLogService activityLogService;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    // Phone validation pattern (supports various formats)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[\\+]?[1-9]?[0-9]{7,15}$"
    );

    public boolean registerPatient(Patient patient, String currentUser) {
        try {
            // Validate patient data
            if (!validatePatient(patient)) {
                return false;
            }

            // Check for duplicate phone number
            if (isPhoneNumberExists(patient.getPhoneNumber(), patient.getPatientID())) {
                addErrorMessage("Registration Failed", "Phone number already exists in the system.");
                return false;
            }

            // Check for duplicate email if provided
            if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()
                    && isEmailExists(patient.getEmail(), patient.getPatientID())) {
                addErrorMessage("Registration Failed", "Email address already exists in the system.");
                return false;
            }

            patientDAO.save(patient);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "PATIENT_REGISTERED",
                        "Registered patient: " + patient.getFirstName() + " " + patient.getLastName());
            }

            addSuccessMessage("Success", "Patient registered successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Registration Failed", "Error occurred while registering patient: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePatient(Patient patient, String currentUser) {
        try {
            if (!validatePatient(patient)) {
                return false;
            }

            // Check for duplicate phone number (excluding current patient)
            if (isPhoneNumberExists(patient.getPhoneNumber(), patient.getPatientID())) {
                addErrorMessage("Update Failed", "Phone number already exists in the system.");
                return false;
            }

            // Check for duplicate email if provided (excluding current patient)
            if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()
                    && isEmailExists(patient.getEmail(), patient.getPatientID())) {
                addErrorMessage("Update Failed", "Email address already exists in the system.");
                return false;
            }

            patientDAO.save(patient);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "PATIENT_UPDATED",
                        "Updated patient: " + patient.getFirstName() + " " + patient.getLastName());
            }

            addSuccessMessage("Success", "Patient updated successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Update Failed", "Error occurred while updating patient: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePatient(Patient patient, String currentUser) {
        try {
            if (patient == null) {
                addErrorMessage("Delete Failed", "Patient not found.");
                return false;
            }

            // Check if patient has appointments
            if (hasActiveAppointments(patient.getPatientID())) {
                addErrorMessage("Delete Failed", "Cannot delete patient with active appointments. Please cancel appointments first.");
                return false;
            }

            patientDAO.delete(patient);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "PATIENT_DELETED",
                        "Deleted patient: " + patient.getFirstName() + " " + patient.getLastName());
            }

            addSuccessMessage("Success", "Patient deleted successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Delete Failed", "Error occurred while deleting patient: " + e.getMessage());
            return false;
        }
    }

    public Patient getPatient(int id) {
        try {
            return patientDAO.findById(id);
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving patient: " + e.getMessage());
            return null;
        }
    }

    public List<Patient> getAllPatients() {
        try {
            return patientDAO.findAll();
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving patients: " + e.getMessage());
            return List.of(); // Return empty list instead of null
        }
    }

    public List<Patient> searchPatients(String searchTerm) {
        try {
            return patientDAO.searchPatients(searchTerm);
        } catch (Exception e) {
            addErrorMessage("Search Error", "Error searching patients: " + e.getMessage());
            return List.of();
        }
    }

    private boolean validatePatient(Patient patient) {
        if (patient == null) {
            addErrorMessage("Validation Error", "Patient data is required.");
            return false;
        }

        // Validate first name
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            addErrorMessage("Validation Error", "First name is required.");
            return false;
        }
        if (patient.getFirstName().length() > 50) {
            addErrorMessage("Validation Error", "First name must be less than 50 characters.");
            return false;
        }

        // Validate last name
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            addErrorMessage("Validation Error", "Last name is required.");
            return false;
        }
        if (patient.getLastName().length() > 50) {
            addErrorMessage("Validation Error", "Last name must be less than 50 characters.");
            return false;
        }

        // Validate date of birth
        if (patient.getDateOfBirth() == null) {
            addErrorMessage("Validation Error", "Date of birth is required.");
            return false;
        }
        if (patient.getDateOfBirth().isAfter(LocalDate.now())) {
            addErrorMessage("Validation Error", "Date of birth cannot be in the future.");
            return false;
        }

        // Check if patient is too old (reasonable limit)
        int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
        if (age > 150) {
            addErrorMessage("Validation Error", "Invalid date of birth - age cannot exceed 150 years.");
            return false;
        }

        // Validate gender
        if (patient.getGender() == null) {
            addErrorMessage("Validation Error", "Gender is required.");
            return false;
        }

        // Validate phone number
        if (patient.getPhoneNumber() == null || patient.getPhoneNumber().trim().isEmpty()) {
            addErrorMessage("Validation Error", "Phone number is required.");
            return false;
        }
        if (!PHONE_PATTERN.matcher(patient.getPhoneNumber().replaceAll("[\\s\\-$]", "")).matches()) {
            addErrorMessage("Validation Error", "Invalid phone number format.");
            return false;
        }

        // Validate email if provided
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(patient.getEmail()).matches()) {
                addErrorMessage("Validation Error", "Invalid email format.");
                return false;
            }
        }

        // Validate address length
        if (patient.getAddress() != null && patient.getAddress().length() > 100) {
            addErrorMessage("Validation Error", "Address must be less than 100 characters.");
            return false;
        }

        // Validate emergency contact length
        if (patient.getEmergencyContact() != null && patient.getEmergencyContact().length() > 50) {
            addErrorMessage("Validation Error", "Emergency contact must be less than 50 characters.");
            return false;
        }

        return true;
    }

    private boolean isPhoneNumberExists(String phoneNumber, int excludePatientId) {
        List<Patient> patients = getAllPatients();
        return patients.stream()
                .anyMatch(p -> p.getPatientID() != excludePatientId &&
                        p.getPhoneNumber().equals(phoneNumber));
    }

    private boolean isEmailExists(String email, int excludePatientId) {
        List<Patient> patients = getAllPatients();
        return patients.stream()
                .anyMatch(p -> p.getPatientID() != excludePatientId &&
                        email.equalsIgnoreCase(p.getEmail()));
    }

    private boolean hasActiveAppointments(int patientId) {
        // This would need to be implemented with a proper query
        // For now, return false - in real implementation, check appointments table
        return false;
    }


    public void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    public void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }
}