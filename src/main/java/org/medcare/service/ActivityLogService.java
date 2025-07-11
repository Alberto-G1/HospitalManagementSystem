package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.ActivityLogDAO;
import org.medcare.models.ActivityLog;
import org.medcare.models.User;

import java.util.List;

@ApplicationScoped
public class ActivityLogService {

    @Inject
    private ActivityLogDAO activityLogDAO;

    public void log(String action, String details, User user) {
        ActivityLog log = new ActivityLog(user, action, details);
        activityLogDAO.save(log);
    }

    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogDAO.findRecent(limit);
    }

    public List<ActivityLog> getAllActivities() {
        return activityLogDAO.findAll();
    }
}