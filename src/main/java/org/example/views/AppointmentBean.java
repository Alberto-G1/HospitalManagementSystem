package org.example.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.example.enums.AppointmentStatus;
import org.example.models.Appointment;
import org.example.models.Doctor;
import org.example.models.Patient;
import org.example.service.AppointmentService;
import org.example.service.DoctorService;
import org.example.service.PatientService;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class AppointmentBean implements Serializable {

    private Appointment appointment = new Appointment();
    private Appointment selectedAppointment;
    private String appointmentDateStr;
    private String appointmentTimeStr;
    private String searchTerm = "";
    private AppointmentStatus filterStatus;
    private Date filterStartDate;
    private Date filterEndDate;

    private List<Appointment> appointments;
    private List<Appointment> filteredAppointments;
    private List<Doctor> doctors;
    private List<Doctor> availableDoctors;
    private List<Patient> patients;

    private boolean editMode = false;
    private boolean showBookingDialog = false;

    @Inject
    private AppointmentService appointmentService;

    @Inject
    private DoctorService doctorService;

    @Inject
    private PatientService patientService;

    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        appointments = appointmentService.getAllAppointments();
        filteredAppointments = appointments;
        doctors = doctorService.getAllDoctors();
        availableDoctors = doctorService.getAvailableDoctors();
        patients = patientService.getAllPatients();
    }

    public String bookAppointment() {
        try {
            if (!parseDateTime()) {
                return null;
            }

            if (appointmentService.bookAppointment(appointment, userBean.getUser().getUsername())) {
                resetForm();
                loadData();
                showBookingDialog = false;
                return "appointments.xhtml?faces-redirect=true";
            }
            return null;

        } catch (Exception e) {
            appointmentService.addErrorMessage("Booking Error", "Error occurred: " + e.getMessage());
            return null;
        }
    }

    public String updateAppointment() {
        try {
            if (!parseDateTime()) {
                return null;
            }

            if (appointmentService.updateAppointment(appointment, userBean.getUser().getUsername())) {
                resetForm();
                loadData();
                editMode = false;
                return "appointments.xhtml?faces-redirect=true";
            }
            return null;

        } catch (Exception e) {
            appointmentService.addErrorMessage("Update Error", "Error occurred: " + e.getMessage());
            return null;
        }
    }

    public String cancelAppointment(int appointmentId) {
        Appointment appt = appointmentService.getAppointment(appointmentId);
        if (appt != null && appointmentService.cancelAppointment(appt, userBean.getUser().getUsername())) {
            loadData();
        }
        return "appointments.xhtml?faces-redirect=true";
    }

    public String completeAppointment(int appointmentId) {
        Appointment appt = appointmentService.getAppointment(appointmentId);
        if (appt != null && appointmentService.completeAppointment(appt, userBean.getUser().getUsername())) {
            loadData();
        }
        return "appointments.xhtml?faces-redirect=true";
    }

    public String deleteAppointment(int appointmentId) {
        Appointment appt = appointmentService.getAppointment(appointmentId);
        if (appt != null && appointmentService.deleteAppointment(appt, userBean.getUser().getUsername())) {
            loadData();
        }
        return "appointments.xhtml?faces-redirect=true";
    }

    public void editAppointment(Appointment appt) {
        this.appointment = new Appointment();
        // Copy properties to avoid reference issues
        this.appointment.setAppointmentID(appt.getAppointmentID());
        this.appointment.setPatient(appt.getPatient());
        this.appointment.setDoctor(appt.getDoctor());
        this.appointment.setAppointmentDate(appt.getAppointmentDate());
        this.appointment.setAppointmentTime(appt.getAppointmentTime());
        this.appointment.setReason(appt.getReason());
        this.appointment.setStatus(appt.getStatus());

        // Set string representations for form binding
        this.appointmentDateStr = appt.getAppointmentDate() != null ?
                appt.getAppointmentDate().toLocalDate().toString() : "";
        this.appointmentTimeStr = appt.getAppointmentTime() != null ?
                appt.getAppointmentTime().toLocalTime().toString() : "";

        this.editMode = true;
    }

    public void viewDetails(Appointment appt) {
        selectedAppointment = appt;
    }

    public void openBookingDialog() {
        resetForm();
        showBookingDialog = true;
    }

    public void closeBookingDialog() {
        showBookingDialog = false;
        resetForm();
    }

    public void search() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            applyFilters();
        } else {
//            filteredAppointments = appointmentService.searchAppointments(searchTerm.trim());
            applyStatusFilter();
            applyDateRangeFilter();
        }
    }

    public void applyFilters() {
        filteredAppointments = appointments;
        applyStatusFilter();
        applyDateRangeFilter();
    }

    private void applyStatusFilter() {
        if (filterStatus != null) {
            filteredAppointments = filteredAppointments.stream()
                    .filter(appt -> appt.getStatus() == filterStatus)
                    .collect(Collectors.toList());
        }
    }

    private void applyDateRangeFilter() {
        if (filterStartDate != null && filterEndDate != null) {
            filteredAppointments = filteredAppointments.stream()
                    .filter(appt -> !appt.getAppointmentDate().before(filterStartDate) &&
                            !appt.getAppointmentDate().after(filterEndDate))
                    .collect(Collectors.toList());
        }
    }

    public void clearFilters() {
        searchTerm = "";
        filterStatus = null;
        filterStartDate = null;
        filterEndDate = null;
        filteredAppointments = appointments;
    }

    public void cancelEdit() {
        resetForm();
        editMode = false;
    }

    private void resetForm() {
        appointment = new Appointment();
        appointmentDateStr = "";
        appointmentTimeStr = "";
        selectedAppointment = null;
    }

    private boolean parseDateTime() {
        try {
            if (appointmentDateStr != null && !appointmentDateStr.isEmpty()) {
                appointment.setAppointmentDate(Date.valueOf(LocalDate.parse(appointmentDateStr)));
            }

            if (appointmentTimeStr != null && !appointmentTimeStr.isEmpty()) {
                appointment.setAppointmentTime(Time.valueOf(LocalTime.parse(appointmentTimeStr)));
            }

            return true;

        } catch (DateTimeParseException e) {
            appointmentService.addErrorMessage("Invalid Date/Time",
                    "Please enter valid date (YYYY-MM-DD) and time (HH:MM) formats.");
            return false;
        }
    }

    // Utility methods for UI
    public List<AppointmentStatus> getStatusOptions() {
        return Arrays.asList(AppointmentStatus.values());
    }

    public String getStatusColor(AppointmentStatus status) {
        switch (status) {
            case SCHEDULED: return "blue";
            case COMPLETED: return "green";
            case CANCELED: return "red";
            default: return "gray";
        }
    }

    public boolean canEditAppointment(Appointment appt) {
        return userBean.isAdmin() ||
                (userBean.isReceptionist() && appt.getStatus() == AppointmentStatus.SCHEDULED);
    }

    public boolean canCancelAppointment(Appointment appt) {
        return (userBean.isAdmin() || userBean.isReceptionist()) &&
                appt.getStatus() == AppointmentStatus.SCHEDULED;
    }

    public boolean canCompleteAppointment(Appointment appt) {
        return (userBean.isAdmin() || userBean.isDoctor()) &&
                appt.getStatus() == AppointmentStatus.SCHEDULED;
    }

    public boolean canDeleteAppointment() {
        return userBean.isAdmin();
    }

    public String getMinDate() {
        return LocalDate.now().toString();
    }

    public List<String> getTimeSlots() {
        return Arrays.asList(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00"
        );
    }

    // Getters and Setters
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public Appointment getSelectedAppointment() { return selectedAppointment; }
    public void setSelectedAppointment(Appointment selectedAppointment) { this.selectedAppointment = selectedAppointment; }

    public List<Appointment> getAppointments() { return appointments; }
    public List<Appointment> getFilteredAppointments() { return filteredAppointments; }

    public List<Doctor> getDoctors() { return doctors; }
    public List<Doctor> getAvailableDoctors() { return availableDoctors; }
    public List<Patient> getPatients() { return patients; }

    public String getAppointmentDateStr() { return appointmentDateStr; }
    public void setAppointmentDateStr(String appointmentDateStr) { this.appointmentDateStr = appointmentDateStr; }

    public String getAppointmentTimeStr() { return appointmentTimeStr; }
    public void setAppointmentTimeStr(String appointmentTimeStr) { this.appointmentTimeStr = appointmentTimeStr; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public AppointmentStatus getFilterStatus() { return filterStatus; }
    public void setFilterStatus(AppointmentStatus filterStatus) { this.filterStatus = filterStatus; }

    public Date getFilterStartDate() { return filterStartDate; }
    public void setFilterStartDate(Date filterStartDate) { this.filterStartDate = filterStartDate; }

    public Date getFilterEndDate() { return filterEndDate; }
    public void setFilterEndDate(Date filterEndDate) { this.filterEndDate = filterEndDate; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    public boolean isShowBookingDialog() { return showBookingDialog; }
    public void setShowBookingDialog(boolean showBookingDialog) { this.showBookingDialog = showBookingDialog; }
}