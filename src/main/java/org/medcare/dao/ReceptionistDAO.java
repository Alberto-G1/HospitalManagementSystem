package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.Receptionist;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class ReceptionistDAO extends GenericDAO<Receptionist> {
    public ReceptionistDAO() {
        super(Receptionist.class);
    }

    // Find a Receptionist profile based on the User's ID
    public Receptionist findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Receptionist r WHERE r.user.userId = :userId AND r.user.active = true", Receptionist.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Method to find receptionist by user ID including inactive ones
    public Receptionist findByUserIdIncludeInactive(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Receptionist r WHERE r.user.userId = :userId", Receptionist.class)
                    .setParameter("userId", userId)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Override to only find active receptionists
    @Override
    public List<Receptionist> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Receptionist r WHERE r.user.active = true", Receptionist.class).list();
        }
    }

    // Method to find all receptionists including inactive ones
    public List<Receptionist> findAllIncludeInactive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Receptionist r", Receptionist.class).list();
        }
    }

    // Override findById to only return active receptionists
    @Override
    public Receptionist findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Receptionist r WHERE r.receptionistId = :id AND r.user.active = true", Receptionist.class)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElse(null);
        }
    }

    // Method to find by ID including inactive receptionists
    public Receptionist findByIdIncludeInactive(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Receptionist.class, id);
        }
    }
}
