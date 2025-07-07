package org.medcare.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.medcare.utils.HibernateUtil;
import java.util.List;

public class GenericDAO<T> {
    private final Class<T> clazz;
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void saveOrUpdate(T entity) {
        executeWithinTransaction(session -> session.saveOrUpdate(entity));
    }

    public void save(T entity) {
        executeWithinTransaction(session -> session.save(entity));
    }

    public void delete(T entity) {
        executeWithinTransaction(session -> session.delete(entity));
    }

    public T findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(clazz, id);
        }
    }

    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + clazz.getSimpleName(), clazz).list();
        }
    }

    /**
     * Utility method to handle transactions for common operations.
     */
    private void executeWithinTransaction(EntityOperation operation) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface EntityOperation {
        void accept(Session session);
    }
}
