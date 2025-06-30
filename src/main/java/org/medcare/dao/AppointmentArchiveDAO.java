package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.AppointmentArchive;

@ApplicationScoped
public class AppointmentArchiveDAO extends GenericDAO<AppointmentArchive> {
    public AppointmentArchiveDAO() {
        super(AppointmentArchive.class);
    }
}