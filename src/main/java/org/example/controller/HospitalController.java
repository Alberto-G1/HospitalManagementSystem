package org.example.controller;

import org.example.enums.Gender;
import org.example.models.Appointment;
import org.example.models.Doctor;
import org.example.models.Patient;
import org.example.enums.Availability;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class HospitalController {
//    private List<Patient> patients = new ArrayList<>();
//    private List<Doctor> doctors = new ArrayList<>();
//    private List<Appointment> appointments = new ArrayList<>();


    public void registerPatient(String firstName, String lastName, String dob, String gender, String address, String phone, String email, String emergencyContact) {
        Patient p = new Patient();

        // Set the user inputs to their respective variables
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setDateOfBirth(dob.toString());
        p.setGender(Gender.valueOf(gender));
        p.setAddress(address);
        p.setPhoneNumber(phone);
        p.setEmail(email);
        p.setEmergencyContact(emergencyContact);

        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            session.save(p);

            tx.commit();

            System.out.println("Patient " + p.getPatientID() + " successfully saved ");
        } catch (Exception e) {
            if (tx != null) tx.rollback(); // Undoes the partial save
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

    }


    public void addDoctor(String firstName, String lastName, String specialty, String phone, String email, Availability isAvailable) {
        Doctor d  = new Doctor();

        // Set the user inputs to their respective variables
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setSpeciality(specialty);
        d.setPhoneNumber(phone);
        d.setEmail(email);
        d.setIsAvailable(isAvailable);

        Transaction tx = null;
        Session session  = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();

            tx = session.beginTransaction();

            session.save(d);

            tx.commit();

            System.out.println("Patient successfully saved " + d.getDoctorID());
        } catch (Exception e){
            if (tx != null){
                e.printStackTrace();
            }
        } finally {
            if (session != null) session.close();
        }
//        int id = doctors.size() + 1;
//        doctors.add(new Doctor(id, firstName, lastName, specialty, phone, email));
//        System.out.println("Doctor added: ID=" + id);
    }

    public void bookAppointment(int patientId, int doctorId, String reason) {
        Transaction tx = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Fetch existing patient and doctor
            Patient patient = (Patient) session.get(Patient.class, patientId);
            Doctor doctor = (Doctor) session.get(Doctor.class, doctorId);

            // Validate existence
            if (patient == null || doctor == null) {
                System.out.println("Invalid Patient or Doctor ID.");
                return;
            }

            // Create and save the appointment with actual entities
            Appointment appointment = new Appointment();
            session.save(appointment);

            tx.commit();

            System.out.println("✔✔Appointment booked successfully......");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }


//    public void listPatients() {
//        for (Patient p : patients) {
//            System.out.println(p.getPatientID() + ". " + p.getFirstName() + " " + p.getLastName() +
//                    " | DOB: " + p.getDateOfBirth() + " | Phone: " + p.getPhoneNumber());
//        }
//    }

    public void listPatients() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            List<Patient> patients = session.createQuery("FROM Patient").list();

            for (Patient p : patients) {
                System.out.println(p.getPatientID() + ". " + p.getFirstName() + " " + p.getLastName() +
                        " | DOB: " + p.getDateOfBirth() + " | Phone: " + p.getPhoneNumber());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    public void listDoctors() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            List<Doctor> doctors = session.createQuery("FROM Doctor").list();

            for (Doctor d : doctors) {
                System.out.println(d.getDoctorID() + ". Dr. " + d.getFirstName() + " " + d.getLastName() +
                        " - " + d.getSpeciality());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

//    public void listDoctors() {
//        for (Doctor d : doctors) {
//            System.out.println(d.getDoctorID() + ". Dr. " + d.getFirstName() + " " + d.getLastName() +
//                    " - " + d.getSpeciality());
//        }
//    }
}