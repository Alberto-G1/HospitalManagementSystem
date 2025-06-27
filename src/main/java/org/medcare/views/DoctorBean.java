package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Availability;
import org.medcare.models.Doctor;
import org.medcare.service.DoctorService;

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
        try {
            System.out.println("DoctorBean init() called");
            loadDoctors();
            System.out.println("DoctorBean init() completed. Doctors loaded: " +
                    (doctors != null ? doctors.size() : 0));
        } catch (Exception e) {
            System.err.println("Error in DoctorBean init(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getAvailabilityClass(Doctor doc) {
        if (doc == null || doc.getIsAvailable() == null) return "unknown";
        return "availability-" + doc.getIsAvailable().toString().toLowerCase();
    }

    public void loadDoctors() {
        try {
            System.out.println("Loading doctors...");
            doctors = doctorService.getAllDoctors();
            filteredDoctors = doctors;
            availableDoctors = doctorService.getAvailableDoctors();
            System.out.println("Doctors loaded successfully. Count: " +
                    (doctors != null ? doctors.size() : 0));
        } catch (Exception e) {
            System.err.println("Error loading doctors: " + e.getMessage());
            e.printStackTrace();
            // Initialize empty lists to prevent null pointer exceptions
            doctors = Arrays.asList();
            filteredDoctors = Arrays.asList();
            availableDoctors = Arrays.asList();
        }
    }

    public String addDoctor() {
        try {
            System.out.println("Adding doctor: " + doctor.getFirstName() + " " + doctor.getLastName());

            if (userBean == null || !userBean.isLoggedIn()) {
                doctorService.addErrorMessage("Add Failed", "User not logged in");
                return null;
            }

            if (doctorService.addDoctor(doctor, userBean.getUser().getUsername())) {
                resetForm();
                loadDoctors();
                showAddForm = false;
                System.out.println("Doctor added successfully");
                return null; // Stay on same page to avoid ViewExpiredException
            }
            return null; // Stay on same page if validation fails
        } catch (Exception e) {
            System.err.println("Error adding doctor: " + e.getMessage());
            e.printStackTrace();
            doctorService.addErrorMessage("Add Failed", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public String updateDoctor() {
        try {
            System.out.println("Updating doctor ID: " + doctor.getDoctorID());

            if (userBean == null || !userBean.isLoggedIn()) {
                doctorService.addErrorMessage("Update Failed", "User not logged in");
                return null;
            }

            if (doctorService.updateDoctor(doctor, userBean.getUser().getUsername())) {
                resetForm();
                loadDoctors();
                editMode = false;
                System.out.println("Doctor updated successfully");
                return null; // Stay on same page to avoid ViewExpiredException
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
            doctorService.addErrorMessage("Update Failed", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public void editDoctor(Doctor d) {
        try {
            System.out.println("Editing doctor: " + d.getFirstName() + " " + d.getLastName());
            this.doctor = new Doctor();

            // Copy properties to avoid direct reference issues
            this.doctor.setDoctorID(d.getDoctorID());
            this.doctor.setFirstName(d.getFirstName());
            this.doctor.setLastName(d.getLastName());
            this.doctor.setSpeciality(d.getSpeciality());
            this.doctor.setPhoneNumber(d.getPhoneNumber());
            this.doctor.setEmail(d.getEmail());
            this.doctor.setIsAvailable(d.getIsAvailable());
            this.doctor.setAddedBy(d.getAddedBy());
            this.doctor.setCreatedAt(d.getCreatedAt());
            this.doctor.setUpdatedAt(d.getUpdatedAt());

            this.editMode = true;
            this.showAddForm = false;
            System.out.println("Doctor set for editing. Edit mode: " + editMode);
        } catch (Exception e) {
            System.err.println("Error setting doctor for edit: " + e.getMessage());
            e.printStackTrace();
            doctorService.addErrorMessage("Edit Error", "Error preparing doctor for edit: " + e.getMessage());
        }
    }

    public String deleteDoctor(int id) {
        try {
            System.out.println("Deleting doctor with ID: " + id);

            if (userBean == null || !userBean.isLoggedIn()) {
                doctorService.addErrorMessage("Delete Failed", "User not logged in");
                return null;
            }

            Doctor d = doctorService.getDoctor(id);
            if (d != null && doctorService.deleteDoctor(d, userBean.getUser().getUsername())) {
                loadDoctors();
                System.out.println("Doctor deleted successfully");
            } else if (d == null) {
                doctorService.addErrorMessage("Delete Failed", "Doctor not found");
            }
            return null; // Stay on same page to avoid ViewExpiredException
        } catch (Exception e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
            doctorService.addErrorMessage("Delete Failed", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public void search() {
        try {
            System.out.println("Searching doctors with term: " + searchTerm);
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                applyFilters();
            } else {
                List<Doctor> searchResults = doctorService.searchDoctors(searchTerm.trim());
                filteredDoctors = applyFiltersToList(searchResults);
            }
            System.out.println("Search completed. Results: " +
                    (filteredDoctors != null ? filteredDoctors.size() : 0));
        } catch (Exception e) {
            System.err.println("Error searching doctors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void applyFilters() {
        try {
            filteredDoctors = applyFiltersToList(doctors);
            System.out.println("Filters applied. Results: " +
                    (filteredDoctors != null ? filteredDoctors.size() : 0));
        } catch (Exception e) {
            System.err.println("Error applying filters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Doctor> applyFiltersToList(List<Doctor> doctorList) {
        if (doctorList == null) {
            return Arrays.asList();
        }

        return doctorList.stream()
                .filter(d -> filterSpeciality == null || filterSpeciality.isEmpty() ||
                        d.getSpeciality().toLowerCase().contains(filterSpeciality.toLowerCase()))
                .filter(d -> filterAvailability == null || d.getIsAvailable() == filterAvailability)
                .collect(Collectors.toList());
    }

    public void clearFilters() {
        try {
            searchTerm = "";
            filterSpeciality = "";
            filterAvailability = null;
            filteredDoctors = doctors;
            System.out.println("Filters cleared");
        } catch (Exception e) {
            System.err.println("Error clearing filters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewDetails(Doctor d) {
        try {
            selectedDoctor = d;
            System.out.println("Selected doctor for details: " +
                    (d != null ? d.getFirstName() + " " + d.getLastName() : "null"));
        } catch (Exception e) {
            System.err.println("Error viewing doctor details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showAddDoctorForm() {
        try {
            resetForm();
            showAddForm = true;
            editMode = false;
            System.out.println("Add doctor form shown");
        } catch (Exception e) {
            System.err.println("Error showing add form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void hideAddDoctorForm() {
        try {
            showAddForm = false;
            resetForm();
            System.out.println("Add doctor form hidden");
        } catch (Exception e) {
            System.err.println("Error hiding add form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cancelEdit() {
        try {
            resetForm();
            editMode = false;
            showAddForm = false;
            System.out.println("Edit cancelled");
        } catch (Exception e) {
            System.err.println("Error cancelling edit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetForm() {
        doctor = new Doctor();
        selectedDoctor = null;
    }

    public void toggleAvailability(Doctor d) {
        try {
            System.out.println("Toggling availability for doctor: " + d.getFirstName() + " " + d.getLastName());

            if (userBean == null || !userBean.isLoggedIn()) {
                doctorService.addErrorMessage("Update Failed", "User not logged in");
                return;
            }

            d.setIsAvailable(d.getIsAvailable() == Availability.AVAILABLE ?
                    Availability.UNAVAILABLE : Availability.AVAILABLE);

            if (doctorService.updateDoctor(d, userBean.getUser().getUsername())) {
                loadDoctors();
                doctorService.addSuccessMessage("Success",
                        "Dr. " + d.getFirstName() + " " + d.getLastName() +
                                " is now " + d.getIsAvailable().toString().toLowerCase());
                System.out.println("Availability toggled successfully");
            }
        } catch (Exception e) {
            System.err.println("Error toggling availability: " + e.getMessage());
            e.printStackTrace();
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
        try {
            if (doctors == null || doctors.isEmpty()) {
                return Arrays.asList();
            }
            return doctors.stream()
                    .map(Doctor::getSpeciality)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting unique specialities: " + e.getMessage());
            return Arrays.asList();
        }
    }

    public String getAvailabilityLabel(Availability availability) {
        return availability == Availability.AVAILABLE ? "Available" : "Unavailable";
    }

    public String getAvailabilityStyle(Availability availability) {
        return availability == Availability.AVAILABLE ?
                "color: #28a745; font-weight: bold;" :
                "color: #dc3545; font-weight: bold;";
    }

    // Permission methods
    public boolean canEditDoctor() {
        return userBean != null && userBean.isAdmin();
    }

    public boolean canDeleteDoctor() {
        return userBean != null && userBean.isAdmin();
    }

    public boolean canAddDoctor() {
        return userBean != null && userBean.isAdmin();
    }

    public boolean canToggleAvailability() {
        return userBean != null && (userBean.isAdmin() || userBean.isDoctor());
    }

    // Getter methods for JSF EL compatibility
    public boolean getCanAddDoctor() { return canAddDoctor(); }
    public boolean getCanEditDoctor() { return canEditDoctor(); }
    public boolean getCanDeleteDoctor() { return canDeleteDoctor(); }
    public boolean getCanToggleAvailability() { return canToggleAvailability(); }

    // Statistics methods
    public int getTotalDoctors() {
        return doctors != null ? doctors.size() : 0;
    }

    public int getAvailableDoctorsCount() {
        return availableDoctors != null ? availableDoctors.size() : 0;
    }

    public int getUnavailableDoctorsCount() {
        return getTotalDoctors() - getAvailableDoctorsCount();
    }

    public String getDoctorFullName(Doctor doctor) {
        if (doctor == null) return "Unknown Doctor";
        return "Dr. " + doctor.getFirstName() + " " + doctor.getLastName();
    }

    public String getMostCommonSpeciality() {
        try {
            if (doctors == null || doctors.isEmpty()) {
                return "N/A";
            }
            return doctors.stream()
                    .collect(Collectors.groupingBy(Doctor::getSpeciality, Collectors.counting()))
                    .entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
                    .orElse("N/A");
        } catch (Exception e) {
            System.err.println("Error getting most common speciality: " + e.getMessage());
            return "N/A";
        }
    }

    // Getters and Setters
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(Doctor selectedDoctor) { this.selectedDoctor = selectedDoctor; }

    public List<Doctor> getDoctors() {
        return doctors != null ? doctors : Arrays.asList();
    }

    public List<Doctor> getFilteredDoctors() {
        return filteredDoctors != null ? filteredDoctors : Arrays.asList();
    }

    public List<Doctor> getAvailableDoctors() {
        return availableDoctors != null ? availableDoctors : Arrays.asList();
    }

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