package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;

    @NotNull(message = "Bill is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @NotNull(message = "Service name is required.")
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters.")
    @Column(nullable = false, length = 100)
    private String serviceName;

    @Size(max = 500, message = "Description must be less than 500 characters.")
    @Column(length = 500)
    private String description;

    @Min(value = 1, message = "Quantity must be at least 1.")
    @Column(nullable = false)
    private int quantity;

    @NotNull(message = "Unit price is required.")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0.")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "Total amount is required.")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0.")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    // Constructors
    public BillItem() {}

    public BillItem(String serviceName, int quantity, BigDecimal unitPrice) {
        this.serviceName = serviceName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }


    // Calculate total amount
    public void calculateTotal() {
        if (unitPrice != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }


    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotal();
    }


    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
