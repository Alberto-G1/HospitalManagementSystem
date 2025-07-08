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

    public void save(T entity) {
        executeWithinTransaction(session -> {
            session.persist(entity);
            session.flush();
        });
    }

    public void update(T entity) {
        executeWithinTransaction(session -> {
            session.merge(entity);
            session.flush();
        });
    }

    public void saveOrUpdate(T entity) {
        executeWithinTransaction(session -> {
            session.merge(entity);
            session.flush();
        });
    }

    public void delete(T entity) {
        executeWithinTransaction(session -> {
            // Re-attach the entity to the session if it's detached
            T managedEntity = session.merge(entity);
            session.remove(managedEntity);
            session.flush();
        });
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
    protected void executeWithinTransaction(EntityOperation operation) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    System.err.println("Error during rollback: " + rollbackException.getMessage());
                }
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    protected interface EntityOperation {
        void accept(Session session);
    }
}
