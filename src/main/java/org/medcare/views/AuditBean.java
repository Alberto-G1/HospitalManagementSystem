package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.dao.ActivityLogDAO;
import org.medcare.models.ActivityLog;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class AuditBean implements Serializable {

    @Inject
    private ActivityLogDAO activityLogDAO;

    private List<ActivityLog> logs;
    private List<ActivityLog> allLogsForFiltering; // A cache to get distinct users/actions from

    // --- Filter fields ---
    private String globalFilter;
    private String selectedUser;
    private String selectedAction;
    private LocalDate startDate;
    private LocalDate endDate;

    @PostConstruct
    public void init() {
        // Load all logs once to populate dropdowns and for the initial view
        allLogsForFiltering = activityLogDAO.findAll();
        logs = allLogsForFiltering;
    }

    /**
     * Called when the user clicks the "Filter" button.
     * It uses the filter criteria to fetch a new, filtered list of logs.
     */
    public void filterLogs() {
        logs = activityLogDAO.findWithFilters(globalFilter, selectedUser, selectedAction, startDate, endDate);
    }

    /**
     * Clears all filter fields and reloads the complete log list.
     */
    public void clearFilters() {
        globalFilter = null;
        selectedUser = null;
        selectedAction = null;
        startDate = null;
        endDate = null;
        logs = allLogsForFiltering; // Restore from the initial cache
    }

    /**
     * Provides a distinct list of usernames for the filter dropdown.
     */
    public List<String> getDistinctUsers() {
        return allLogsForFiltering.stream()
                .map(log -> log.getUser().getUsername())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Provides a distinct list of action types for the filter dropdown.
     */
    public List<String> getDistinctActions() {
        return allLogsForFiltering.stream()
                .map(ActivityLog::getAction)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // --- Getters and Setters for bean and filter fields ---
    public List<ActivityLog> getLogs() { return logs; }
    public String getGlobalFilter() { return globalFilter; }
    public void setGlobalFilter(String globalFilter) { this.globalFilter = globalFilter; }
    public String getSelectedUser() { return selectedUser; }
    public void setSelectedUser(String selectedUser) { this.selectedUser = selectedUser; }
    public String getSelectedAction() { return selectedAction; }
    public void setSelectedAction(String selectedAction) { this.selectedAction = selectedAction; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}