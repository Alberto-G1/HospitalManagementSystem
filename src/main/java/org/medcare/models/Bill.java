package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet; // Import HashSet
import java.util.Set;    // Import Set

@Entity
@Table(name = "bills")
public class Bill extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int billId;

    @NotNull(message = "An appointment must be linked to the bill.")
    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;

    @NotNull(message = "Bill date is required.")
    @Column(nullable = false)
    private LocalDate billDate;

    @NotNull(message = "Bill status is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<BillItem> billItems = new HashSet<>();

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Payment> payments = new HashSet<>();

    public enum BillStatus {
        DRAFT, FINALIZED, PARTIALLY_PAID, PAID, VOIDED
    }

    // Transient methods for calculations
    @Transient
    public BigDecimal getTotalAmount() {
        return billItems.stream()
                .map(BillItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getTotalPaid() {
        return payments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getBalanceDue() {
        return getTotalAmount().subtract(getTotalPaid());
    }

    // Getters and Setters (updated to use Set)
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public Set<BillItem> getBillItems() { return billItems; }
    public void setBillItems(Set<BillItem> billItems) { this.billItems = billItems; }
    public Set<Payment> getPayments() { return payments; }
    public void setPayments(Set<Payment> payments) { this.payments = payments; }
}