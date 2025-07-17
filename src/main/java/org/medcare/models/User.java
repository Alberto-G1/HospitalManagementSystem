package org.medcare.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.medcare.enums.Role;

import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @NotBlank(message = "Username is required.")
    @Size(max = 30, message = "Username must be less than 30 characters.")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters.")
    @Column(nullable = false)
    private String password;

    @Email(message = "Invalid email format.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email must end with a valid domain (e.g., .com, .org)")
    @NotBlank(message = "Email is required.")
    @Size(max = 50, message = "Email must be less than 50 characters.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Role is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}