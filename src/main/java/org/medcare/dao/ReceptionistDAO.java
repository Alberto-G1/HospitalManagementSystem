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
            // Find all receptionists whose associated user account is active
            return session.createQuery("FROM Receptionist r WHERE r.user.active = true", Receptionist.class).list();
        }
    }
}