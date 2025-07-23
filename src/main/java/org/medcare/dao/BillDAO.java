package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Bill;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class BillDAO extends GenericDAO<Bill> {
    public BillDAO() { super(Bill.class); }

    @Override
    public List<Bill> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT b FROM Bill b " +
                                    "LEFT JOIN FETCH b.appointment a " +
                                    "LEFT JOIN FETCH a.patient " +
                                    "LEFT JOIN FETCH a.doctor " +
                                    "ORDER BY b.billDate DESC", Bill.class)
                    .list();
        }
    }

    @Override
    public Bill findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT b FROM Bill b " +
                                    "LEFT JOIN FETCH b.billItems " +
                                    "LEFT JOIN FETCH b.payments " +
                                    "LEFT JOIN FETCH b.appointment a " +
                                    "LEFT JOIN FETCH a.patient " +
                                    "LEFT JOIN FETCH a.doctor " +
                                    "WHERE b.billId = :id", Bill.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }
}