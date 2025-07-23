package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.medcare.dao.AppointmentDAO;
import org.medcare.dao.BillDAO;
import org.medcare.dao.PaymentDAO;
import org.medcare.enums.Role;
import org.medcare.models.Appointment;
import org.medcare.models.Bill;
import org.medcare.models.Bill.BillStatus;
import org.medcare.models.Payment;
import org.medcare.models.User;
import org.medcare.service.interfaces.ActivityLogServiceInterface;
import org.medcare.service.interfaces.BillServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class BillService implements BillServiceInterface {
    @Inject private BillDAO billDAO;
    @Inject private AppointmentDAO appointmentDAO;
    @Inject private PaymentDAO paymentDAO;
    @Inject private ActivityLogServiceInterface activityLogService;

    @Override
    @Transactional
    public Bill createBillFromAppointment(int appointmentId, User creator) {
        Objects.requireNonNull(creator, "Creator cannot be null.");
        if (creator.getRole() != Role.ADMIN && creator.getRole() != Role.RECEPTIONIST) {
            throw new SecurityException("Only Admins or Receptionists can create bills.");
        }

        Appointment appointment = appointmentDAO.findById(appointmentId);
        if (appointment == null) {
            throw new ValidationException("Appointment not found.");
        }

        // Check if a bill already exists for this appointment
        List<Bill> existingBills = billDAO.findAll();
        boolean billExists = existingBills.stream().anyMatch(b -> b.getAppointment().getAppointmentId() == appointmentId);
        if (billExists) {
            throw new ValidationException("A bill already exists for this appointment.");
        }

        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setBillDate(LocalDate.now());
        bill.setStatus(BillStatus.DRAFT); // Default status
        bill.setCreatedBy(creator);
        bill.setLastUpdatedBy(creator);

        billDAO.save(bill);
        activityLogService.log("BILL_CREATED", "Created draft bill #" + bill.getBillId() + " from appointment #" + appointmentId, creator);
        return bill;
    }

    @Override
    @Transactional
    public Bill updateBill(Bill bill, User updater) {
        Objects.requireNonNull(bill, "Bill cannot be null.");
        Objects.requireNonNull(updater, "Updater cannot be null.");

        Bill existingBill = billDAO.findById(bill.getBillId());
        if (existingBill == null) {
            throw new ValidationException("Bill not found for update.");
        }
        if (existingBill.getStatus() != BillStatus.DRAFT) {
            throw new ValidationException("Only bills in DRAFT status can be edited.");
        }

        // Update items (Hibernate manages this via cascade)
        existingBill.getBillItems().clear();
        existingBill.getBillItems().addAll(bill.getBillItems());
        for (var item : existingBill.getBillItems()) {
            item.setBill(existingBill);
        }

        existingBill.setLastUpdatedBy(updater);
        billDAO.update(existingBill);
        activityLogService.log("BILL_UPDATED", "Updated draft bill #" + bill.getBillId(), updater);
        return existingBill;
    }

    @Override
    @Transactional
    public Bill finalizeBill(int billId, User finalizer) {
        Objects.requireNonNull(finalizer, "Finalizer cannot be null.");
        Bill bill = billDAO.findById(billId);
        if (bill == null) throw new ValidationException("Bill not found.");
        if (bill.getStatus() != BillStatus.DRAFT) throw new ValidationException("Only DRAFT bills can be finalized.");
        if (bill.getBillItems().isEmpty()) throw new ValidationException("Cannot finalize a bill with no items.");

        bill.setStatus(BillStatus.FINALIZED);
        bill.setLastUpdatedBy(finalizer);
        billDAO.update(bill);
        activityLogService.log("BILL_FINALIZED", "Finalized bill #" + billId, finalizer);
        return bill;
    }

    @Override
    @Transactional
    public Bill voidBill(int billId, String justification, User admin) {
        Objects.requireNonNull(admin, "Admin user cannot be null.");
        if (admin.getRole() != Role.ADMIN) throw new SecurityException("Only Admins can void bills.");
        if (justification == null || justification.trim().isEmpty()) throw new ValidationException("A justification is required to void a bill.");

        Bill bill = billDAO.findById(billId);
        if (bill == null) throw new ValidationException("Bill not found.");
        if (bill.getStatus() == BillStatus.PAID) throw new ValidationException("Cannot void a fully PAID bill.");

        bill.setStatus(BillStatus.VOIDED);
        bill.setJustification(justification);
        bill.setActive(false); // Soft delete
        bill.setLastUpdatedBy(admin);
        billDAO.update(bill);
        activityLogService.log("BILL_VOIDED", "Voided bill #" + billId + " with reason: " + justification, admin);
        return bill;
    }

    @Override
    @Transactional
    public Payment addPayment(Payment payment, User cashier) {
        Objects.requireNonNull(payment, "Payment object cannot be null.");
        Objects.requireNonNull(cashier, "Cashier cannot be null.");

        Bill bill = billDAO.findById(payment.getBill().getBillId());
        if (bill == null) throw new ValidationException("Bill not found.");
        if (bill.getStatus() != BillStatus.FINALIZED && bill.getStatus() != BillStatus.PARTIALLY_PAID) {
            throw new ValidationException("Payments can only be added to FINALIZED or PARTIALLY_PAID bills.");
        }

        BigDecimal balanceDue = bill.getBalanceDue();
        if (payment.getAmountPaid().compareTo(balanceDue) > 0) {
            throw new ValidationException("Payment amount cannot exceed the balance due of " + balanceDue);
        }

        payment.setBill(bill);
        payment.setProcessedBy(cashier);
        paymentDAO.save(payment);

        bill.getPayments().add(payment); // This ensures getBalanceDue() is accurate for the check

        // Now, update bill status based on the ACCURATE new balance
        BigDecimal newBalance = bill.getBalanceDue();
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }
        billDAO.update(bill);

        activityLogService.log("PAYMENT_ADDED", "Added payment of " + payment.getAmountPaid() + " to bill #" + bill.getBillId(), cashier);
        return payment;
    }

    @Override
    public Bill getBillById(int id) {
        if (id <= 0) return null;
        return billDAO.findById(id);
    }

    @Override
    public List<Bill> getAllBills() {
        return billDAO.findAll();
    }
}