package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "receptionists")
public class Receptionist extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int receptionistId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", unique = true)
    private User user;

    @NotBlank(message = "First name is required.")
    @Size(max = 50, message = "First name must be less than 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters.")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 50, message = "Last name must be less than 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters.")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^(\\+256|0)7[0-9]{8}$", message = "Invalid Ugandan phone number.")
    @Column(nullable = false)
    private String phoneNumber;

    // Getters and Setters
    public int getReceptionistId() { return receptionistId; }
    public void setReceptionistId(int receptionistId) { this.receptionistId = receptionistId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}