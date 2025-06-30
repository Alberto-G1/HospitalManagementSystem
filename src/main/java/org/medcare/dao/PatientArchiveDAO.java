package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.PatientArchive;

@ApplicationScoped
public class PatientArchiveDAO extends GenericDAO<PatientArchive> {
    public PatientArchiveDAO() {
        super(PatientArchive.class);
    }
}