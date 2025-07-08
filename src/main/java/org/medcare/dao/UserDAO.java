package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.User;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class UserDAO extends GenericDAO<User> {
    public UserDAO() {
        super(User.class);
    }

    // Override to only find active users by default
    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE active = true", User.class).list();
        }
    }

    // Method to find all users including inactive ones (for admin purposes)
    public List<User> findAllIncludeInactive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    // This method is now used for login, so it MUST only find active users.
    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :username AND active = true", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // New method for admin purposes to find a user regardless of active status
    public User findByUsernameIncludeInactive(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email AND active = true", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Method to find by email including inactive users
    public User findByEmailIncludeInactive(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Special method to update user active status with proper transaction handling
    public void updateUserActiveStatus(int userId, boolean active) {
        executeWithinTransaction(session -> {
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setActive(active);
                session.merge(user);
                session.flush();
            }
        });
    }
}