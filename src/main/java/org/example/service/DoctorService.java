package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.dao.DoctorDAO;
import org.example.enums.Availability;
import org.example.models.Doctor;

import java.util.List;
import java.util.regex.Pattern;

@Named
@ApplicationScoped
public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();

    @Inject
    private ActivityLogService activityLogService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[\\+]?[1-9]?[0-9]{7,15}$"
    );

    public boolean addDoctor(Doctor doctor, String currentUser) {
        try {
            if (!validateDoctor(doctor)) {
                return false;
            }

            // Check for duplicate email
            if (isEmailExists(doctor.getEmail(), doctor.getDoctorID())) {
                addErrorMessage("Registration Failed", "Email address already exists in the system.");
                return false;
            }

            // Check for duplicate phone number
            if (isPhoneNumberExists(doctor.getPhoneNumber(), doctor.getDoctorID())) {
                addErrorMessage("Registration Failed", "Phone number already exists in the system.");
                return false;
            }

            doctorDAO.save(doctor);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "DOCTOR_ADDED",
                        "Added doctor: Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
            }

            addSuccessMessage("Success", "Doctor added successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Registration Failed", "Error occurred while adding doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor, String currentUser) {
        try {
            if (!validateDoctor(doctor)) {
                return false;
            }

            // Check for duplicate email (excluding current doctor)
            if (isEmailExists(doctor.getEmail(), doctor.getDoctorID())) {
                addErrorMessage("Update Failed", "Email address already exists in the system.");
                return false;
            }

            // Check for duplicate phone number (excluding current doctor)
            if (isPhoneNumberExists(doctor.getPhoneNumber(), doctor.getDoctorID())) {
                addErrorMessage("Update Failed", "Phone number already exists in the system.");
                return false;
            }

            doctorDAO.save(doctor);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "DOCTOR_UPDATED",
                        "Updated doctor: Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
            }

            addSuccessMessage("Success", "Doctor updated successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Update Failed", "Error occurred while updating doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDoctor(Doctor doctor, String currentUser) {
        try {
            if (doctor == null) {
                addErrorMessage("Delete Failed", "Doctor not found.");
                return false;
            }

            // Check if doctor has appointments
            if (hasActiveAppointments(doctor.getDoctorID())) {
                addErrorMessage("Delete Failed", "Cannot delete doctor with active appointments. Please reassign or cancel appointments first.");
                return false;
            }

            doctorDAO.delete(doctor);

            // Log activity
            if (currentUser != null) {
                activityLogService.logActivity(currentUser, "DOCTOR_DELETED",
                        "Deleted doctor: Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
            }

            addSuccessMessage("Success", "Doctor deleted successfully!");
            return true;

        } catch (Exception e) {
            addErrorMessage("Delete Failed", "Error occurred while deleting doctor: " + e.getMessage());
            return false;
        }
    }

    public Doctor getDoctor(int id) {
        try {
            return doctorDAO.findById(id);
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving doctor: " + e.getMessage());
            return null;
        }
    }

    public List<Doctor> getAllDoctors() {
        try {
            return doctorDAO.findAll();
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving doctors: " + e.getMessage());
            return List.of();
        }
    }

    public List<Doctor> getAvailableDoctors() {
        try {
            return doctorDAO.findAvailableDoctors();
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving available doctors: " + e.getMessage());
            return List.of();
        }
    }

    public List<Doctor> searchDoctors(String searchTerm) {
        try {
            return doctorDAO.searchDoctors(searchTerm);
        } catch (Exception e) {
            addErrorMessage("Search Error", "Error searching doctors: " + e.getMessage());
            return List.of();
        }
    }

    private boolean validateDoctor(Doctor doctor) {
        if (doctor == null) {
            addErrorMessage("Validation Error", "Doctor data is required.");
            return false;
        }

        // Validate first name
        if (doctor.getFirstName() == null || doctor.getFirstName().trim().isEmpty()) {
            addErrorMessage("Validation Error", "First name is required.");
            return false;
        }
        if (doctor.getFirstName().length() > 50) {
            addErrorMessage("Validation Error", "First name must be less than 50 characters.");
            return false;
        }

        // Validate last name
        if (doctor.getLastName() == null || doctor.getLastName().trim().isEmpty()) {
            addErrorMessage("Validation Error", "Last name is required.");
            return false;
        }
        if (doctor.getLastName().length() > 50) {
            addErrorMessage("Validation Error", "Last name must be less than 50 characters.");
            return false;
        }

        // Validate speciality
        if (doctor.getSpeciality() == null || doctor.getSpeciality().trim().isEmpty()) {
            addErrorMessage("Validation Error", "Speciality is required.");
            return false;
        }
        if (doctor.getSpeciality().length() > 100) {
            addErrorMessage("Validation Error", "Speciality must be less than 100 characters.");
            return false;
        }

        // Validate phone number
        if (doctor.getPhoneNumber() == null || doctor.getPhoneNumber().trim().isEmpty()) {
            addErrorMessage("Validation Error", "Phone number is required.");
            return false;
        }
        if (!PHONE_PATTERN.matcher(doctor.getPhoneNumber().replaceAll("[\\s\\-$]", "")).matches()) {
            addErrorMessage("Validation Error", "Invalid phone number format.");
            return false;
        }

        // Validate email
        if (doctor.getEmail() == null || doctor.getEmail().trim().isEmpty()) {
            addErrorMessage("Validation Error", "Email is required.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(doctor.getEmail()).matches()) {
            addErrorMessage("Validation Error", "Invalid email format.");
            return false;
        }

        return true;
    }

    private boolean isEmailExists(String email, int excludeDoctorId) {
        List<Doctor> doctors = getAllDoctors();
        return doctors.stream()
                .anyMatch(d -> d.getDoctorID() != excludeDoctorId &&
                        email.equalsIgnoreCase(d.getEmail()));
    }

    private boolean isPhoneNumberExists(String phoneNumber, int excludeDoctorId) {
        List<Doctor> doctors = getAllDoctors();
        return doctors.stream()
                .anyMatch(d -> d.getDoctorID() != excludeDoctorId &&
                        d.getPhoneNumber().equals(phoneNumber));
    }

    private boolean hasActiveAppointments(int doctorId) {
        // This would need to be implemented with a proper query
        // For now, return false - in real implementation, check appointments table
        return false;
    }
    // Add these additional methods to the existing DoctorService class

    public List<Doctor> getDoctorsBySpeciality(String speciality) {
        try {
            return doctorDAO.findBySpeciality(speciality);
        } catch (Exception e) {
            addErrorMessage("Error", "Error retrieving doctors by speciality: " + e.getMessage());
            return List.of();
        }
    }

    public Doctor getDoctorByEmail(String email) {
        try {
            return doctorDAO.findByEmail(email);
        } catch (Exception e) {
            addErrorMessage("Error", "Error finding doctor by email: " + e.getMessage());
            return null;
        }
    }

    public boolean toggleDoctorAvailability(int doctorId, String currentUser) {
        try {
            Doctor doctor = getDoctor(doctorId);
            if (doctor == null) {
                addErrorMessage("Update Failed", "Doctor not found.");
                return false;
            }

            Availability newAvailability = doctor.getIsAvailable() == Availability.AVAILABLE ?
                    Availability.UNAVAILABLE : Availability.AVAILABLE;
            doctor.setIsAvailable(newAvailability);

            return updateDoctor(doctor, currentUser);
        } catch (Exception e) {
            addErrorMessage("Update Failed", "Error toggling availability: " + e.getMessage());
            return false;
        }
    }

    public long countDoctorsBySpeciality(String speciality) {
        try {
            return getDoctorsBySpeciality(speciality).size();
        } catch (Exception e) {
            addErrorMessage("Error", "Error counting doctors by speciality: " + e.getMessage());
            return 0;
        }
    }

    public long countAvailableDoctors() {
        try {
            return getAvailableDoctors().size();
        } catch (Exception e) {
            addErrorMessage("Error", "Error counting available doctors: " + e.getMessage());
            return 0;
        }
    }

    // Make these methods public for use in beans
    public void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    public void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }
}