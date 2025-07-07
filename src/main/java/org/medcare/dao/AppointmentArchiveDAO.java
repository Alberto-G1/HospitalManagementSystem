package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.medcare.models.AppointmentArchive;
import org.medcare.utils.HibernateUtil;
import java.util.List;

@ApplicationScoped
public class AppointmentArchiveDAO extends GenericDAO<AppointmentArchive> {
    public AppointmentArchiveDAO() {
        super(AppointmentArchive.class);
    }

    @Override
    public List<AppointmentArchive> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT aa FROM AppointmentArchive aa " +
                            "LEFT JOIN FETCH aa.archivedBy " +
                            "ORDER BY aa.archivedAt DESC",
                    AppointmentArchive.class
            ).list();
        }
    }
}