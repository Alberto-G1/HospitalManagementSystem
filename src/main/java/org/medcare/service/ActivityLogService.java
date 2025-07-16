package org.medcare.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.medcare.dao.ActivityLogDAO;
import org.medcare.models.ActivityLog;
import org.medcare.models.User;
import org.medcare.service.interfaces.ActivityLogServiceInterface;

import java.util.List;

@ApplicationScoped
public class ActivityLogService implements ActivityLogServiceInterface {

    @Inject
    private ActivityLogDAO activityLogDAO;

    @Override
    public void log(String action, String details, User user) {
        ActivityLog log = new ActivityLog(user, action, details);
        activityLogDAO.save(log);
    }

    @Override
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogDAO.findRecent(limit);
    }

    @Override
    public List<ActivityLog> getAllActivities() {
        return activityLogDAO.findAll();
    }
}