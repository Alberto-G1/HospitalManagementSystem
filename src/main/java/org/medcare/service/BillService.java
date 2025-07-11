package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.BillDAO;
import org.medcare.models.Bill;
import org.medcare.models.User;

import java.util.List;

@ApplicationScoped
public class BillService {
    @Inject
    private BillDAO billDAO;

    @Inject
    private ActivityLogService activityLogService;

    public void saveBill(Bill bill, User user) {
        boolean isNew = bill.getBillId() == 0;
        if (isNew) {
            bill.setCreatedBy(user);
            activityLogService.log("BILL_CREATED", "Created bill #" + bill.getBillId() + " for patient " + bill.getPatient().getFirstName(), user);
        } else {
            bill.setLastUpdatedBy(user);
            activityLogService.log("BILL_UPDATED", "Updated bill #" + bill.getBillId(), user);
        }
        billDAO.saveOrUpdate(bill);
    }

    public Bill getBillById(int id) {
        return billDAO.findById(id);
    }

    public List<Bill> getAllBills() {
        return billDAO.findAll();
    }

    public void deleteBill(Bill bill, User user) {
        billDAO.delete(bill);
        activityLogService.log("BILL_DELETED", "Deleted bill #" + bill.getBillId(), user);
    }
}
