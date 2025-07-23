package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Appointment;
import org.medcare.models.Bill;
import org.medcare.service.interfaces.AppointmentServiceInterface;
import org.medcare.service.interfaces.BillServiceInterface;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class BillingBean implements Serializable {
    @Inject private BillServiceInterface billService;
    @Inject private AppointmentServiceInterface appointmentService;

    private List<Bill> bills;
    private List<Bill> filteredBills;
    private List<Appointment> completableAppointments;

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        bills = billService.getAllBills();
        List<Integer> billedAppointmentIds = bills.stream()
                .map(bill -> bill.getAppointment().getAppointmentId())
                .collect(Collectors.toList());

        completableAppointments = appointmentService.getAllActive().stream()
                .filter(a -> a.getStatus() == org.medcare.enums.AppointmentStatus.COMPLETED)
                .filter(a -> !billedAppointmentIds.contains(a.getAppointmentId()))
                .collect(Collectors.toList());
    }

    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (filterText == null || filterText.isEmpty()) return true;

        Bill bill = (Bill) value;
        return (bill.getAppointment().getPatient().getFirstName().toLowerCase().contains(filterText)) ||
                (bill.getAppointment().getPatient().getLastName().toLowerCase().contains(filterText)) ||
                (bill.getStatus().toString().toLowerCase().contains(filterText)) ||
                (String.valueOf(bill.getBillId()).contains(filterText));
    }

    // Getters and Setters
    public List<Bill> getBills() { return bills; }
    public void setBills(List<Bill> bills) { this.bills = bills; }
    public List<Bill> getFilteredBills() { return filteredBills; }
    public void setFilteredBills(List<Bill> f) { this.filteredBills = f; }
    public List<Appointment> getCompletableAppointments() { return completableAppointments; }
}