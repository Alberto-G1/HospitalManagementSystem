package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.Role;
import org.medcare.models.Appointment;
import org.medcare.models.AppointmentArchive;
import org.medcare.models.Doctor;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.service.interfaces.DoctorServiceInterface;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AppointmentBean implements Serializable {

    @Inject private AppointmentServiceInterface appointmentService;
    @Inject private DoctorServiceInterface doctorService;
    @Inject private UserBean userBean;

    private List<Appointment> appointments;
    private List<AppointmentArchive> archivedAppointments; // Property for archived list
    private List<Appointment> filteredAppointments; // Property for search filter
    private ScheduleModel scheduleModel;

    private Appointment selectedAppointmentForActions;

    @PostConstruct
    public void init() {
        loadAppointments();
        initializeSchedule();
    }

    private void loadAppointments() {
        if (userBean.getUser().getRole() == Role.DOCTOR) {
            Doctor doctorProfile = doctorService.findByUserId(userBean.getUser().getUserId());
            appointments = (doctorProfile != null) ? appointmentService.getAppointmentsForDoctor(doctorProfile.getDoctorId()) : new ArrayList<>();
        } else {
            appointments = appointmentService.getAllActive();
        }

        // FIX: Load archived appointments if the user is an admin
        if (userBean.isAdmin()) {
            archivedAppointments = appointmentService.getAllArchived();
        }
    }

    private void initializeSchedule() {
        scheduleModel = new DefaultScheduleModel();
        if (appointments != null) {
            for (Appointment appt : appointments) {
                LocalDateTime start = LocalDateTime.of(appt.getDate(), appt.getTime());
                LocalDateTime end = start.plusHours(1);
                String title = "Pt: " + appt.getPatient().getLastName() + " | Dr: " + appt.getDoctor().getLastName();
                DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
                        .title(title)
                        .startDate(start)
                        .endDate(end)
                        .data(appt.getAppointmentId())
                        .build();
                scheduleModel.addEvent(event);
            }
        }
    }

    public void onEventSelect(org.primefaces.event.SelectEvent<ScheduleEvent<?>> selectEvent) {
        int appointmentId = (int) selectEvent.getObject().getData();
        try {
            String url = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
                    + "/app/appointment-details.xhtml?id=" + appointmentId;
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Navigation Error", "Could not open appointment details.");
        }
    }

    public void deleteAppointment() {
        try {
            if (selectedAppointmentForActions == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No appointment selected for deletion.");
                return;
            }
            appointmentService.archiveAppointment(selectedAppointmentForActions.getAppointmentId(), userBean.getUser());
            loadAppointments();
            initializeSchedule();
            addMessage(FacesMessage.SEVERITY_WARN, "Success", "Appointment has been deleted and archived.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete appointment: " + e.getMessage());
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (filterText == null || filterText.isEmpty()) return true;
        Appointment appt = (Appointment) value;
        return (appt.getPatient().getFirstName().toLowerCase().contains(filterText)) ||
                (appt.getPatient().getLastName().toLowerCase().contains(filterText)) ||
                (appt.getDoctor().getLastName().toLowerCase().contains(filterText)) ||
                (appt.getStatus().toString().toLowerCase().contains(filterText));
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<Appointment> getAppointments() { return appointments; }
    public ScheduleModel getScheduleModel() { return scheduleModel; }
    public Appointment getSelectedAppointmentForActions() { return selectedAppointmentForActions; }
    public void setSelectedAppointmentForActions(Appointment selectedAppointmentForActions) { this.selectedAppointmentForActions = selectedAppointmentForActions; }
    public List<AppointmentArchive> getArchivedAppointments() { return archivedAppointments; }
    public List<Appointment> getFilteredAppointments() { return filteredAppointments; }
    public void setFilteredAppointments(List<Appointment> filteredAppointments) { this.filteredAppointments = filteredAppointments; }
}