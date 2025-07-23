package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Bill;
import org.medcare.models.Bill.BillStatus;
import org.medcare.models.Payment;
import org.medcare.service.interfaces.BillServiceInterface;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Named
@ViewScoped
public class PaymentBean implements Serializable {

    @Inject
    private BillServiceInterface billService;

    private List<Bill> allBills;
    private List<Payment> allPayments;
    private BigDecimal totalRevenue;
    private BigDecimal totalOutstanding;
    private long paidBillsCount;
    private long partiallyPaidBillsCount;
    private long unpaidBillsCount;

    @PostConstruct
    public void init() {
        allBills = billService.getAllBills();
        calculateStatistics();
    }

    private void calculateStatistics() {
        totalRevenue = BigDecimal.ZERO;
        totalOutstanding = BigDecimal.ZERO;
        paidBillsCount = 0;
        partiallyPaidBillsCount = 0;
        unpaidBillsCount = 0;

        if (allBills != null) {
            for (Bill bill : allBills) {
                if (bill.getStatus() != BillStatus.VOIDED && bill.getStatus() != BillStatus.DRAFT) {
                    BigDecimal paidAmount = bill.getTotalPaid();
                    BigDecimal totalAmount = bill.getTotalAmount();

                    totalRevenue = totalRevenue.add(paidAmount);
                    totalOutstanding = totalOutstanding.add(totalAmount.subtract(paidAmount));

                    if (bill.getStatus() == BillStatus.PAID) {
                        paidBillsCount++;
                    } else if (bill.getStatus() == BillStatus.PARTIALLY_PAID) {
                        partiallyPaidBillsCount++;
                    } else if (bill.getStatus() == BillStatus.FINALIZED) { // Finalized but no payments yet
                        unpaidBillsCount++;
                    }
                }
            }
        }
    }

    // Getters
    public List<Bill> getAllBills() { return allBills; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public long getPaidBillsCount() { return paidBillsCount; }
    public long getPartiallyPaidBillsCount() { return partiallyPaidBillsCount; }
    public long getUnpaidBillsCount() { return unpaidBillsCount; }
}