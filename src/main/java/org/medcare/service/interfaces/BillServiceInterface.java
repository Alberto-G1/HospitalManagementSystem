package org.medcare.service.interfaces;

import org.medcare.models.Bill;
import org.medcare.models.User;

import java.util.List;

public interface BillServiceInterface {
    void saveBill(Bill bill, User user);
    Bill getBillById(int id);
    List<Bill> getAllBills();
    void deleteBill(Bill bill, User user);
}