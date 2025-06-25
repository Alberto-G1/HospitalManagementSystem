package org.example.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.enums.Availability;
import org.example.models.Doctor;
import org.example.service.DoctorService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class DoctorBean implements Serializable {

    private Doctor doctor = new Doctor();
    private Doctor selectedDoctor;
    private String searchTerm = "";
    private String filterSpeciality = "";
    private Availability filterAvailability;
    private List<Doctor> doctors;
    private List<Doctor> filteredDoctors;
    private List<Doctor> availableDoctors;
    private boolean editMode = false;
    private boolean showAddForm = false;

    @Inject
    private DoctorService doctorService;

    @Inject
    private UserBean userBean;

    // Common medical specialities
    private final List<String> commonSpecialities = Arrays.asList(
            "Cardiology", "Dermatology", "Emergency Medicine", "Family Medicine",
            "Gastroenterology", "General Surgery", "Internal Medicine", "Neurology",
            "Obstetrics and Gynecology", "Oncology", "Ophthalmology", "Orthopedics",
            "Otolaryngology", "Pediatrics", "Psychiatry", "Pulmonology",
            "Radiology", "Rheumatology", "Urology", "Anesthesiology"
    );

    @PostConstruct
    public void init() {
        loadDoctors();
    }

    public void loadDoctors() {
        doctors = doctorService.getAllDoctors();
        filteredDoctors = doctors;
        availableDoctors = doctorService.getAvailableDoctors();
    }

    public String addDoctor() {
        try {
            if (doctorService.addDoctor(doctor, userBean.getUser().getUsername())) {
                resetForm();
                loadDoctors();
                showAddForm = false;
                return "doctors.xhtml?faces-redirect=true";
            }
            return null; // Stay on same page if validation fails
        } catch (Exception e) {
            doctorService.addErrorMessage("Add Failed", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public String updateDoctor() {
        try {
            if (doctorService.updateDoctor(doctor, userBean.getUser().getUsername())) {
                resetForm();
                loadDoctors();
                editMode = false;
                return "doctors.xhtml?faces-redirect=true";
            }
            return null;
        } catch (Exception e) {
            doctorService.addErrorMessage("Update Failed", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public void editDoctor(Doctor d) {
        this.doctor = new Doctor();
        // Copy properties to avoid direct reference issues
//        this.doctor.setDoctorID(d.getDoctorID());
        this.doctor.setFirstName(d.getFirstName());
        this.doctor.setLastName(d.getLastName());
        this.doctor.setSpeciality(d.getSpeciality());
        this.doctor.setPhoneNumber(d.getPhoneNumber());
        this.doctor.setEmail(d.getEmail());
        this.doctor.setIsAvailable(d.getIsAvailable());

        this.editMode = true;
        this.showAddForm = false;
    }

    public String deleteDoctor(int id) {
        try {
            Doctor d = doctorService.getDoctor(id);
            if (d != null && doctorService.deleteDoctor(d, userBean.getUser().getUsername())) {
                loadDoctors();
            }
            return "doctors.xhtml?faces-redirect=true";
        } catch (Exception e) {
            doctorService.addErrorMessage("Delete Failed", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public void search() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            applyFilters();
        } else {
            List<Doctor> searchResults = doctorService.searchDoctors(searchTerm.trim());
            filteredDoctors = applyFiltersToList(searchResults);
        }
    }

    public void applyFilters() {
        filteredDoctors = applyFiltersToList(doctors);
    }

    private List<Doctor> applyFiltersToList(List<Doctor> doctorList) {
        return doctorList.stream()
                .filter(d -> filterSpeciality == null || filterSpeciality.isEmpty() ||
                        d.getSpeciality().toLowerCase().contains(filterSpeciality.toLowerCase()))
                .filter(d -> filterAvailability == null || d.getIsAvailable() == filterAvailability)
                .collect(Collectors.toList());
    }

    public void clearFilters() {
        searchTerm = "";
        filterSpeciality = "";
        filterAvailability = null;
        filteredDoctors = doctors;
    }

    public void viewDetails(Doctor d) {
        selectedDoctor = d;
    }

    public void showAddDoctorForm() {
        resetForm();
        showAddForm = true;
        editMode = false;
    }

    public void hideAddDoctorForm() {
        showAddForm = false;
        resetForm();
    }

    public void cancelEdit() {
        resetForm();
        editMode = false;
        showAddForm = false;
    }

    private void resetForm() {
        doctor = new Doctor();
        selectedDoctor = null;
    }

    public void toggleAvailability(Doctor d) {
        try {
            d.setIsAvailable(d.getIsAvailable() == Availability.AVAILABLE ?
                    Availability.UNAVAILABLE : Availability.AVAILABLE);

            if (doctorService.updateDoctor(d, userBean.getUser().getUsername())) {
                loadDoctors();
                doctorService.addSuccessMessage("Success",
                        "Dr. " + d.getFirstName() + " " + d.getLastName() +
                                " is now " + d.getIsAvailable().toString().toLowerCase());
            }
        } catch (Exception e) {
            doctorService.addErrorMessage("Update Failed", "Error updating availability: " + e.getMessage());
        }
    }

    // Utility methods for UI
    public List<Availability> getAvailabilityOptions() {
        return Arrays.asList(Availability.values());
    }

    public List<String> getCommonSpecialities() {
        return commonSpecialities;
    }

    public List<String> getUniqueSpecialities() {
        return doctors.stream()
                .map(Doctor::getSpeciality)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public String getAvailabilityLabel(Availability availability) {
        return availability == Availability.AVAILABLE ? "Available" : "Unavailable";
    }

    public String getAvailabilityStyle(Availability availability) {
        return availability == Availability.AVAILABLE ?
                "color: #28a745; font-weight: bold;" :
                "color: #dc3545; font-weight: bold;";
    }

    public boolean canEditDoctor() {
        return userBean.isAdmin();
    }

    public boolean canDeleteDoctor() {
        return userBean.isAdmin();
    }

    public boolean canAddDoctor() {
        return userBean.isAdmin();
    }

    public boolean canToggleAvailability() {
        return userBean.isAdmin() || userBean.isDoctor();
    }

    public int getTotalDoctors() {
        return doctors != null ? doctors.size() : 0;
    }

    public int getAvailableDoctorsCount() {
        return availableDoctors != null ? availableDoctors.size() : 0;
    }

    public int getUnavailableDoctorsCount() {
        return getTotalDoctors() - getAvailableDoctorsCount();
    }

    public List<Doctor> getDoctorsBySpeciality(String speciality) {
        return doctors.stream()
                .filter(d -> d.getSpeciality().equalsIgnoreCase(speciality))
                .collect(Collectors.toList());
    }

    public String getDoctorFullName(Doctor doctor) {
        return "Dr. " + doctor.getFirstName() + " " + doctor.getLastName();
    }

    public String getDoctorDisplayName(Doctor doctor) {
        return getDoctorFullName(doctor) + " - " + doctor.getSpeciality();
    }

    // Validation helper methods
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    public boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^[\\+]?[1-9]?[0-9]{7,15}$");
    }

    // Statistics methods
    public double getAverageExperienceYears() {
        // This would require an experience field in the Doctor model
        // For now, return a placeholder
        return 8.5;
    }

    public String getMostCommonSpeciality() {
        return doctors.stream()
                .collect(Collectors.groupingBy(Doctor::getSpeciality, Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("N/A");
    }

    // Getters and Setters
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(Doctor selectedDoctor) { this.selectedDoctor = selectedDoctor; }

    public List<Doctor> getDoctors() { return doctors; }
    public List<Doctor> getFilteredDoctors() { return filteredDoctors; }
    public List<Doctor> getAvailableDoctors() { return availableDoctors; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public String getFilterSpeciality() { return filterSpeciality; }
    public void setFilterSpeciality(String filterSpeciality) { this.filterSpeciality = filterSpeciality; }

    public Availability getFilterAvailability() { return filterAvailability; }
    public void setFilterAvailability(Availability filterAvailability) { this.filterAvailability = filterAvailability; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    public boolean isShowAddForm() { return showAddForm; }
    public void setShowAddForm(boolean showAddForm) { this.showAddForm = showAddForm; }
}