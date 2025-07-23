package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Bill;
import org.medcare.models.BillItem;
import org.medcare.models.Payment;
import org.medcare.models.Payment.PaymentMethod;
import org.medcare.service.interfaces.BillServiceInterface;
import org.primefaces.PrimeFaces;
import java.io.Serializable;

@Named
@ViewScoped
public class BillDetailBean implements Serializable {
    @Inject private BillServiceInterface billService;
    @Inject private UserBean userBean;

    private int billId;
    private int appointmentId;
    private Bill bill;

    private BillItem newBillItem = new BillItem();
    private Payment newPayment = new Payment();
    private String voidJustification;

    public void loadBill() {
        if (billId > 0) {
            bill = billService.getBillById(billId);
        } else if (appointmentId > 0) {
            try {
                bill = billService.createBillFromAppointment(appointmentId, userBean.getUser());
                this.billId = bill.getBillId();
            } catch (Exception e) {
                addFlashMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
                FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
                        .handleNavigation(FacesContext.getCurrentInstance(), null, "/app/billing.xhtml?faces-redirect=true");
            }
        }
    }

    public void saveBillItems() {
        try {
            billService.updateBill(bill, userBean.getUser());
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Bill items saved.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void finalizeBill() {
        try {
            bill = billService.finalizeBill(billId, userBean.getUser());
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Bill has been finalized.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void voidBill() {
        try {
            bill = billService.voidBill(billId, voidJustification, userBean.getUser());
            addMessage(FacesMessage.SEVERITY_WARN, "Success", "Bill has been voided.");
            PrimeFaces.current().executeScript("PF('voidBillDialog').hide()");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void addBillItem() {
        if (newBillItem != null && newBillItem.getDescription() != null && !newBillItem.getDescription().isEmpty()) {
            this.bill.getBillItems().add(newBillItem);
            this.newBillItem = new BillItem();
        }
    }

    public void removeBillItem(BillItem item) {
        this.bill.getBillItems().remove(item);
    }

    public void addPayment() {
        try {
            newPayment.setBill(bill);
            billService.addPayment(newPayment, userBean.getUser());
            bill = billService.getBillById(billId);
            newPayment = new Payment();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Payment added.");
            PrimeFaces.current().executeScript("PF('addPaymentDialog').hide()");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    private void addFlashMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        addMessage(severity, summary, detail);
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public Bill getBill() { return bill; }
    public BillItem getNewBillItem() { return newBillItem; }
    public void setNewBillItem(BillItem newBillItem) { this.newBillItem = newBillItem; }
    public Payment getNewPayment() { return newPayment; }
    public void setNewPayment(Payment newPayment) { this.newPayment = newPayment; }
    public String getVoidJustification() { return voidJustification; }
    public void setVoidJustification(String voidJustification) { this.voidJustification = voidJustification; }
    public PaymentMethod[] getPaymentMethods() { return PaymentMethod.values(); }
}