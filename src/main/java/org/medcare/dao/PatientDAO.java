package org.medcare.dao;
import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.Patient;

@ApplicationScoped
public class PatientDAO extends GenericDAO<Patient> {
    public PatientDAO() { super(Patient.class); }
}