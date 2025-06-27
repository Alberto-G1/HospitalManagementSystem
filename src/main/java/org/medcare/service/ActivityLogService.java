package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.medcare.dao.GenericDAO;

import java.util.List;

@Named
@ApplicationScoped
public class ActivityLogService {
    private final GenericDAO<ActivityLog> activityLogDAO = new GenericDAO<ActivityLog>(ActivityLog.class) {};

    public void logActivity(String username, String action, String details) {
        ActivityLog log = new ActivityLog(username, action, details);
        activityLogDAO.save(log);
    }

    public List<ActivityLog> getAllActivities() {
        return activityLogDAO.findAll();
    }

    public List<ActivityLog> getRecentActivities(int limit) {
        // This would need a custom query in a real implementation
        List<ActivityLog> all = getAllActivities();
        return all.size() > limit ? all.subList(0, limit) : all;
    }
}