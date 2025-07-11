package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Bill;
import org.medcare.utils.HibernateUtil;

import java.util.List;

@ApplicationScoped
public class BillDAO extends GenericDAO<Bill> {
    public BillDAO() {
        super(Bill.class);
    }

    @Override
    public List<Bill> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT b FROM Bill b " +
                                    "JOIN FETCH b.patient " +
                                    "LEFT JOIN FETCH b.appointment " +
                                    "ORDER BY b.billDate DESC", Bill.class)
                    .list();
        }
    }
}
