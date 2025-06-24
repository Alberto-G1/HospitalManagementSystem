package org.example.dao;

import org.example.models.Patient;

public class PatientDAO extends GenericDAO<Patient> {
    public PatientDAO() {
        super(Patient.class);
    }
}
