package org.medcare.dao;

import org.medcare.models.User;
import org.medcare.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class UserDAO extends GenericDAO<User> {
    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return (User) session.createQuery("FROM User WHERE username = :username")
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }
}
