package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.User;
import org.medcare.utils.HibernateUtil;

@ApplicationScoped
public class UserDAO extends GenericDAO<User> {
    public UserDAO() { super(User.class); }

    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }
}