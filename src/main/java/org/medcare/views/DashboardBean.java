package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.AppointmentStatus;
import org.medcare.enums.Role;
import org.medcare.models.ActivityLog; // <-- Import the new model
import org.medcare.models.Appointment;
import org.medcare.models.Doctor;
import org.medcare.service.*; // <-- Import all services
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class DashboardBean {

    @Inject private PatientService patientService;
    @Inject private DoctorService doctorService;
    @Inject private AppointmentService appointmentService;
    @Inject private ReceptionistService receptionistService;
    @Inject private UserBean userBean;

    // FIX 1: Inject the ActivityLogService
    @Inject private ActivityLogService activityLogService;


    // --- Data counts ---
    private long patientCount;
    private long doctorCount;
    private long receptionistCount;
    private long appointmentCount;
    private long doctorAppointmentCount;

    // --- Chart Models ---
    private PieChartModel pieModel;
    private PieChartModel receptionistPieModel;
    private PieChartModel doctorPieModel;

    // FIX 2: Add a field to hold the recent activities
    private List<ActivityLog> recentActivities;

    @PostConstruct
    public void init() {
        // Fetch all the counts from the services
        patientCount = patientService.getActivePatientCount();
        doctorCount = doctorService.getAll().size();
        receptionistCount = receptionistService.getAll().size();
        appointmentCount = appointmentService.getAllActive().size();

        // FIX 3: Fetch the recent activities
        recentActivities = activityLogService.getRecentActivities(5); // Get the last 5 activities

        // Create the pie models
        createAdminPieModel();
        createReceptionistPieModel();

        // Handle doctor-specific data
        if (userBean.getUser().getRole() == Role.DOCTOR) {
            Doctor doctorProfile = doctorService.findByUserId(userBean.getUser().getUserId());
            if (doctorProfile != null) {
                List<Appointment> doctorAppointments = appointmentService.getAppointmentsForDoctor(doctorProfile.getDoctorId());
                doctorAppointmentCount = doctorAppointments.size();
                createDoctorPieModel(doctorAppointments);
            }
        }
    }

    // This is a helper method for the view to get the correct icon for an activity
    public String getIconForActivity(String action) {
        if (action == null) return "pi-info-circle";
        if (action.contains("LOGIN")) return "sign-in-alt";
        if (action.contains("PATIENT")) return "user-plus";
        if (action.contains("APPOINTMENT")) return "calendar-check";
        if (action.contains("DOCTOR")) return "user-md";
        if (action.contains("RECEPTIONIST")) return "user-tie";
        if (action.contains("BILL")) return "receipt";
        if (action.contains("RECORD")) return "file-medical";
        return "info-circle";
    }

    private void createAdminPieModel() {
        pieModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(patientCount);
        values.add(doctorCount);
        values.add(receptionistCount);
        values.add(appointmentCount);
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(59, 130, 246)");
        bgColors.add("rgb(16, 185, 129)");
        bgColors.add("rgb(139, 92, 246)");
        bgColors.add("rgb(245, 158, 11)");
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        pieModel.setData(data);
    }

    private void createReceptionistPieModel() {
        receptionistPieModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(patientCount);
        values.add(doctorCount);
        values.add(appointmentCount);
        dataSet.setData(values);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(59, 130, 246)");
        bgColors.add("rgb(16, 185, 129)");
        bgColors.add("rgb(245, 158, 11)");
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        receptionistPieModel.setData(data);
    }

    private void createDoctorPieModel(List<Appointment> appointments) {
        doctorPieModel = new PieChartModel();
        ChartData data = new ChartData();

        Map<AppointmentStatus, Long> statusCounts = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        long scheduled = statusCounts.getOrDefault(AppointmentStatus.SCHEDULED, 0L);
        long completed = statusCounts.getOrDefault(AppointmentStatus.COMPLETED, 0L);
        long canceled = statusCounts.getOrDefault(AppointmentStatus.CANCELED, 0L);

        if (scheduled > 0) {
            values.add(scheduled);
            bgColors.add("rgb(72, 159, 181)");
            labels.add("Scheduled");
        }
        if (completed > 0) {
            values.add(completed);
            bgColors.add("rgb(16, 185, 129)");
            labels.add("Completed");
        }
        if (canceled > 0) {
            values.add(canceled);
            bgColors.add("rgb(239, 68, 68)");
            labels.add("Canceled");
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        doctorPieModel.setData(data);
    }


    // --- Getters ---
    public long getPatientCount() { return patientCount; }
    public long getDoctorCount() { return doctorCount; }
    public long getReceptionistCount() { return receptionistCount; }
    public long getAppointmentCount() { return appointmentCount; }
    public long getDoctorAppointmentCount() { return doctorAppointmentCount; }
    public PieChartModel getPieModel() { return pieModel; }
    public PieChartModel getReceptionistPieModel() { return receptionistPieModel; }
    public PieChartModel getDoctorPieModel() { return doctorPieModel; }

    // FIX 4: Add the public getter for the recentActivities property
    public List<ActivityLog> getRecentActivities() {
        return recentActivities;
    }
}