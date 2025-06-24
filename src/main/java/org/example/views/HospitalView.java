package org.example.views;

import org.example.controller.*;
import org.example.enums.Availability;
import org.example.enums.Gender;
import org.example.enums.Role;
import org.example.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class HospitalView {
    private final Scanner scanner = new Scanner(System.in);
    private final UserController userController = new UserController();
    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();

    public void start() {
        System.out.println("--- Welcome to CityCare General Hospital ---");
        boolean authenticated = false;
        while (!authenticated) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                registerUser();
            } else if ("2".equals(choice)) {
                authenticated = loginUser();
            } else {
                System.out.println("❌ Invalid choice.");
            }
        }
        mainMenu();
    }

    private String promptNonEmpty(String prompt) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("❌ This field is required.");
            }
        }
        return input;
    }

    private void registerUser() {
        String username = promptNonEmpty("Username: ");
        String password = promptNonEmpty("Password: ");
        String email = promptNonEmpty("Email: ");

        Role role = null;
        while (role == null) {
            String roleInput = promptNonEmpty("Role (ADMIN, DOCTOR, PATIENT): ").toUpperCase();
            try {
                role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid role. Please choose ADMIN, DOCTOR, or PATIENT.");
            }
        }

        User user = new User(username, password, email, role);
        userController.register(username, password, email, role);
    }

    private boolean loginUser() {
        String username = promptNonEmpty("Username: ");
        String password = promptNonEmpty("Password: ");
        return userController.login(username, password);
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Register Patient");
            System.out.println("2. Add Doctor");
            System.out.println("3. Book Appointment");
            System.out.println("4. List Patients");
            System.out.println("5. List Doctors");
            System.out.println("6. List Appointments");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("❌ Please enter a valid choice.");
                continue;
            }

            switch (choice) {
                case "1": registerPatient(); break;
                case "2": addDoctor(); break;
                case "3": bookAppointment(); break;
                case "4": listPatients(); break;
                case "5": listDoctors(); break;
                case "6": listAppointments(); break;
                case "0": running = false; System.out.println("Logged out."); break;
                default: System.out.println("❌ Invalid choice."); break;
            }
        }
    }

    private void registerPatient() {
        String firstName = promptNonEmpty("First name: ");
        String lastName = promptNonEmpty("Last name: ");
        LocalDate dob = null;
        while (dob == null) {
            String dobInput = promptNonEmpty("Date of Birth (YYYY-MM-DD): ");
            try {
                dob = LocalDate.parse(dobInput);
            } catch (Exception e) {
                System.out.println("❌ Invalid date.");
            }
        }

        Gender gender = null;
        while (gender == null) {
            String genderInput = promptNonEmpty("Gender (MALE/FEMALE/OTHER): ").toUpperCase();
            try {
                gender = Gender.valueOf(genderInput);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid gender. Please choose MALE, FEMALE, or OTHER.");
            }
        }

        String address = promptNonEmpty("Address: ");
        String phone = promptNonEmpty("Phone: ");
        String email = promptNonEmpty("Email: ");
        String emergency = promptNonEmpty("Emergency Contact: ");

        Patient p = new Patient();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setDateOfBirth(dob.toString());
        p.setGender(gender);
        p.setAddress(address);
        p.setPhoneNumber(phone);
        p.setEmail(email);
        p.setEmergencyContact(emergency);

        patientController.registerPatient(p);
        System.out.println("✅ Patient successfully registered...................");
    }

    private void addDoctor() {
        String firstName = promptNonEmpty("First name: ");
        String lastName = promptNonEmpty("Last name: ");
        String specialty = promptNonEmpty("Speciality: ");
        String phone = promptNonEmpty("Phone: ");
        String email = promptNonEmpty("Email: ");

        Doctor d = new Doctor();
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setSpeciality(specialty);
        d.setPhoneNumber(phone);
        d.setEmail(email);
        d.setIsAvailable(Availability.AVAILABLE);

        doctorController.addDoctor(d);
    }

    private void bookAppointment() {
        listPatients();
        if (patientController.listPatients().isEmpty()) return;
        int pid = Integer.parseInt(promptNonEmpty("Patient ID: "));

        listDoctors();
        if (doctorController.listDoctors().isEmpty()) return;
        int did = Integer.parseInt(promptNonEmpty("Doctor ID: "));

        String reason = promptNonEmpty("Reason: ");

        Patient p = patientController.listPatients().stream().filter(x -> x.getPatientID() == pid).findFirst().orElse(null);
        Doctor d = doctorController.listDoctors().stream().filter(x -> x.getDoctorID() == did).findFirst().orElse(null);

        if (p == null || d == null) {
            System.out.println("❌ Invalid Patient/Doctor ID.");
            return;
        }

        Appointment a = new Appointment(p, d, reason);
        appointmentController.bookAppointment(a);
    }

    private void listPatients() {
        List<Patient> patients = patientController.listPatients();
        if (patients.isEmpty()) {
            System.out.println("ℹ No patients found.");
            return;
        }
        for (Patient p : patients) {
            System.out.println(p.getPatientID() + " - " + p.getFirstName() + " " + p.getLastName());
        }
    }

    private void listDoctors() {
        List<Doctor> doctors = doctorController.listDoctors();
        if (doctors.isEmpty()) {
            System.out.println("ℹ No doctors found.");
            return;
        }
        for (Doctor d : doctors) {
            System.out.println(d.getDoctorID() + " - Dr. " + d.getFirstName() + " " + d.getLastName());
        }
    }

    private void listAppointments() {
        List<Appointment> appointments = appointmentController.listAppointments();
        if (appointments.isEmpty()) {
            System.out.println("ℹ No appointments found.");
            return;
        }
        for (Appointment a : appointments) {
            System.out.println(a.getAppointmentID() + ": " + a.getPatient().getFirstName() + " with Dr. " +
                    a.getDoctor().getFirstName() + " for " + a.getReason());
        }
    }
}
