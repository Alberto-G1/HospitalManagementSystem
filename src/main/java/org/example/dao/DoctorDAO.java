package org.example.dao;

import org.example.models.Doctor;

public class DoctorDAO extends GenericDAO<Doctor> {
    public DoctorDAO() {
        super(Doctor.class);
    }
}
