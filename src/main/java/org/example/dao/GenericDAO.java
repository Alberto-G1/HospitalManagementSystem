package org.example.dao;

import org.example.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public abstract class GenericDAO<T> {
    private final Class<T> clazz;

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void save(T entity) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void delete(T entity) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public T findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return (T) session.get(clazz, id);
        } finally {
            System.out.println("Got the id");
        }
    }

    public List<T> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("FROM " + clazz.getSimpleName()).list();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    public static class PatientDAO {
    }
}
