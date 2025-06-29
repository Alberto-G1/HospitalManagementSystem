package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.medcare.enums.Gender;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class Patient {

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

    // NEW FIELD: Tracks which user created this patient record.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;


    // --- Getters and Setters ---
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
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}