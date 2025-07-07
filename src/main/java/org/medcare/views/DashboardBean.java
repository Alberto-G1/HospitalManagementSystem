package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.AppointmentStatus;
import org.medcare.enums.Role;
import org.medcare.models.Appointment;
import org.medcare.models.Doctor;
import org.medcare.service.AppointmentService;
import org.medcare.service.DoctorService;
import org.medcare.service.PatientService;
import org.medcare.service.ReceptionistService;
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

    // --- Data counts ---
    private long patientCount;
    private long doctorCount;
    private long receptionistCount;
    private long appointmentCount;
    private long doctorAppointmentCount; // New for doctor's view

    // --- Chart Models ---
    private PieChartModel pieModel;
    private PieChartModel receptionistPieModel;
    private PieChartModel doctorPieModel;


    @PostConstruct
    public void init() {
        // Fetch all the counts from the services
        patientCount = patientService.getAllActive().size();
        doctorCount = doctorService.getAll().size();
        receptionistCount = receptionistService.getAll().size();
        appointmentCount = appointmentService.getAllActive().size();

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
        bgColors.add("rgb(59, 130, 246)");   // Patients
        bgColors.add("rgb(16, 185, 129)");   // Doctors
        bgColors.add("rgb(139, 92, 246)");  // Receptionists
        bgColors.add("rgb(245, 158, 11)");   // Appointments
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
        bgColors.add("rgb(59, 130, 246)");   // Patients
        bgColors.add("rgb(16, 185, 129)");   // Doctors
        bgColors.add("rgb(245, 158, 11)");   // Appointments
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        receptionistPieModel.setData(data);
    }

    private void createDoctorPieModel(List<Appointment> appointments) {
        doctorPieModel = new PieChartModel();
        ChartData data = new ChartData();

        // Group appointments by status and count them
        Map<AppointmentStatus, Long> statusCounts = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Add data for each status type, ensuring a consistent order
        long scheduled = statusCounts.getOrDefault(AppointmentStatus.SCHEDULED, 0L);
        long completed = statusCounts.getOrDefault(AppointmentStatus.COMPLETED, 0L);
        long canceled = statusCounts.getOrDefault(AppointmentStatus.CANCELED, 0L);

        if (scheduled > 0) {
            values.add(scheduled);
            bgColors.add("rgb(72, 159, 181)"); // Scheduled
            labels.add("Scheduled");
        }
        if (completed > 0) {
            values.add(completed);
            bgColors.add("rgb(16, 185, 129)"); // Completed
            labels.add("Completed");
        }
        if (canceled > 0) {
            values.add(canceled);
            bgColors.add("rgb(239, 68, 68)");  // Canceled
            labels.add("Canceled");
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels); // Set labels for the legend
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
}