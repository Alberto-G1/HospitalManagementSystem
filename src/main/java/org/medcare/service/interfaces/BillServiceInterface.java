package org.medcare.service.interfaces;

import org.medcare.models.Bill;
import org.medcare.models.Payment;
import org.medcare.models.User;
import java.util.List;

public interface BillServiceInterface {
    Bill createBillFromAppointment(int appointmentId, User creator);
    Bill updateBill(Bill bill, User updater);
    Bill finalizeBill(int billId, User finalizer);
    Bill voidBill(int billId, String justification, User admin);
    Payment addPayment(Payment payment, User cashier);

    Bill getBillById(int id);
    List<Bill> getAllBills();
}