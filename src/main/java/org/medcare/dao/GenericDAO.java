package org.medcare.dao;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.medcare.utils.HibernateUtil;

import java.util.List;

public class GenericDAO<T> {
    private final Class<T> clazz;
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Transactional
    public void saveOrUpdate(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(entity);
        session.getTransaction().commit();
    }

    @Transactional
    public void delete(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(entity);
        session.getTransaction().commit();
    }

    public T findById(int id) {
        Session session = sessionFactory.openSession();
        T result = session.get(clazz, id);
        session.close();
        return result;
    }

    public List<T> findAll() {
        Session session = sessionFactory.openSession();
        List<T> list = session.createQuery("from " + clazz.getSimpleName(), clazz).list();
        session.close();
        return list;
    }
}
