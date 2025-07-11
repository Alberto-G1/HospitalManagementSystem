package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.medcare.enums.Gender;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int patientId;

    @NotBlank(message = "First name is required.")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotNull(message = "Date of Birth is required.")
    @Past(message = "Date of Birth must be in the past.")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^(\\+256|0)7[0-9]{8}$", message = "Invalid Ugandan phone number.")
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Email(message = "Invalid email format.")
    @Column(length = 50)
    private String email;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Appointment> appointments;

    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
    public Collection<Appointment> getAppointments() { return appointments; }
    public void setAppointments(Collection<Appointment> appointments) { this.appointments = appointments; }
}
