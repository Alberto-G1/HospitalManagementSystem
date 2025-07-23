package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @NotNull(message = "Payment amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be positive.")
    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal amountPaid;

    @NotNull(message = "Payment method is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String cardDetails; // e.g., "Visa **** 1234"

    @Pattern(regexp = "^(\\+256|0)7[0-9]{8}$", message = "Invalid Ugandan mobile money number.")
    private String mobileMoneyNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @NotNull
    private LocalDateTime paymentDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id", nullable = false)
    private User processedBy;

    // Enum for PaymentMethod
    public enum PaymentMethod {
        CASH, CARD, MOBILE_MONEY
    }

    @PrePersist
    protected void onPersist() {
        this.paymentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCardDetails() { return cardDetails; }
    public void setCardDetails(String cardDetails) { this.cardDetails = cardDetails; }
    public String getMobileMoneyNumber() { return mobileMoneyNumber; }
    public void setMobileMoneyNumber(String mobileMoneyNumber) { this.mobileMoneyNumber = mobileMoneyNumber; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public User getProcessedBy() { return processedBy; }
    public void setProcessedBy(User processedBy) { this.processedBy = processedBy; }
}