package org.medcare.service.interfaces;

import org.medcare.models.ActivityLog;
import org.medcare.models.User;

import java.util.List;

public interface ActivityLogServiceInterface {
    void log(String action, String details, User user);
    List<ActivityLog> getRecentActivities(int limit);
    List<ActivityLog> getAllActivities();
}