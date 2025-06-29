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

    // Transactions are now better handled at the Service layer.
    // This method remains for direct DAO-level operations.
    public void saveOrUpdate(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
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
}