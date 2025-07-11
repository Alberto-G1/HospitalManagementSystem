package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.enums.PaymentStatus;
import org.medcare.models.Appointment;
import org.medcare.models.Bill;
import org.medcare.models.Patient;
import org.medcare.service.AppointmentService;
import org.medcare.service.BillService;
import org.medcare.service.PatientService;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class BillBean implements Serializable {

    @Inject private BillService billService;
    @Inject private PatientService patientService;
    @Inject private AppointmentService appointmentService;
    @Inject private UserBean userBean;

    private List<Bill> bills;
    private List<Patient> activePatients;
    private List<Appointment> appointmentsWithoutBills;
    private Bill selectedBill;

    @PostConstruct
    public void init() {
        bills = billService.getAllBills();
        activePatients = patientService.getAllActive();

        List<Integer> billedAppointmentIds = bills.stream()
                .filter(b -> b.getAppointment() != null)
                .map(b -> b.getAppointment().getAppointmentId())
                .collect(Collectors.toList());

        appointmentsWithoutBills = appointmentService.getAllActive().stream()
                .filter(a -> !billedAppointmentIds.contains(a.getAppointmentId()))
                .collect(Collectors.toList());
    }

    public void openNew() {
        selectedBill = new Bill();
        selectedBill.setBillDate(LocalDate.now());
        selectedBill.setAmount(BigDecimal.ZERO);
        selectedBill.setStatus(PaymentStatus.PENDING);
    }

    public void saveBill() {
        try {
            billService.saveBill(selectedBill, userBean.getUser());
            init(); // Refresh list
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Bill saved successfully.");
            PrimeFaces.current().executeScript("PF('manageBillDialog').hide()");
            PrimeFaces.current().ajax().update("billForm:messages", "billForm:dt-bills");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save bill.");
            e.printStackTrace();
        }
    }

    public void deleteBill() {
        try {
            billService.deleteBill(selectedBill, userBean.getUser());
            init();
            addMessage(FacesMessage.SEVERITY_WARN, "Deleted", "Bill has been deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete bill.");
        }
    }

    public void onAppointmentChange() {
        if (selectedBill != null && selectedBill.getAppointment() != null) {
            selectedBill.setPatient(selectedBill.getAppointment().getPatient());
            // Optionally auto-fill description or amount
            selectedBill.setDescription("Consultation with Dr. " + selectedBill.getAppointment().getDoctor().getLastName());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<Bill> getBills() { return bills; }
    public void setBills(List<Bill> bills) { this.bills = bills; }
    public List<Patient> getActivePatients() { return activePatients; }
    public List<Appointment> getAppointmentsWithoutBills() { return appointmentsWithoutBills; }
    public Bill getSelectedBill() { return selectedBill; }
    public void setSelectedBill(Bill selectedBill) { this.selectedBill = selectedBill; }
    public PaymentStatus[] getPaymentStatuses() { return PaymentStatus.values(); }
}
