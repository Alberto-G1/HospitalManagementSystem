package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.medcare.models.ActivityLog;
import org.medcare.utils.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ActivityLogDAO extends GenericDAO<ActivityLog> {
    public ActivityLogDAO() {
        super(ActivityLog.class);
    }

    public List<ActivityLog> findRecent(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT al FROM ActivityLog al LEFT JOIN FETCH al.user ORDER BY al.timestamp DESC", ActivityLog.class)
                    .setMaxResults(limit)
                    .list();
        }
    }

    @Override
    public List<ActivityLog> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT al FROM ActivityLog al LEFT JOIN FETCH al.user ORDER BY al.timestamp DESC", ActivityLog.class)
                    .list();
        }
    }

    /**
     * Finds ActivityLogs based on a dynamic set of filter criteria.
     * Uses JPA Criteria API for safe and flexible query building.
     */
    public List<ActivityLog> findWithFilters(String globalFilter, String selectedUser, String selectedAction, LocalDate startDate, LocalDate endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var cb = session.getCriteriaBuilder();
            var cq = cb.createQuery(ActivityLog.class);
            var root = cq.from(ActivityLog.class);
            root.fetch("user"); // Eager fetch user to prevent N+1 queries

            List<Predicate> predicates = new ArrayList<>();

            // Date Range Filter
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("timestamp"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startDate.atStartOfDay()));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), endDate.atTime(LocalTime.MAX)));
            }

            // Specific User Filter
            if (selectedUser != null && !selectedUser.isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("username"), selectedUser));
            }

            // Specific Action Filter
            if (selectedAction != null && !selectedAction.isEmpty()) {
                predicates.add(cb.equal(root.get("action"), selectedAction));
            }

            // Global "any word" filter
            if (globalFilter != null && !globalFilter.trim().isEmpty()) {
                String pattern = "%" + globalFilter.trim().toLowerCase() + "%";
                Predicate userMatch = cb.like(cb.lower(root.get("user").get("username")), pattern);
                Predicate actionMatch = cb.like(cb.lower(root.get("action")), pattern);
                Predicate detailsMatch = cb.like(cb.lower(root.get("details")), pattern);
                predicates.add(cb.or(userMatch, actionMatch, detailsMatch));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cq.orderBy(cb.desc(root.get("timestamp")));

            return session.createQuery(cq).getResultList();
        }
    }
}