package com.project.backend.security;

import com.google.cloud.Timestamp;
import com.project.backend.firebase.CollectionName;
import com.project.backend.model.Model;

/**
 * Specifies the name of the Firebase Firestore collection that stores
 * security-related details.
 */
@CollectionName("SecurityDetails")
public class AuthenticationDetails extends Model {
    // User's email address
    private String email;
    // User's hashed password
    private String password;
    // User's role (TEACHER, STUDENT)
    private String role;
    //The timestamp of the user's last logout from the application.
    private Timestamp lastLogout;
    // Protected no-argument constructor for Firestore serialization
    protected AuthenticationDetails() {}

    // Constructor to create an AuthenticationDetails object with email, password, role, and userId
    public AuthenticationDetails(String userId, String email, String password, UserRole userRole) {
        this.email = email;
        // Encode the password using the BCryptPasswordEncoder from BackendSecurityConfiguration
        this.password = BackendSecurityConfiguration.encoder.encode(password);
        this.role = userRole.name();
        this.setId(userId);
        lastLogout = Timestamp.now();
    }
    //Getter for lastLogout
    public Timestamp getLastLogout() {
        return lastLogout;
    }
    //Setter for lastLogout
    public void setLastLogout(Timestamp lastLogout) {
        this.lastLogout = lastLogout;
    }

    // Getter for the password
    public String getPassword() {
        return password;
    }

    // Setter for the password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for the role
    public String getRole() {
        return role;
    }

    // Setter for the role, accepting a UserRole enum
    public void setRole(UserRole userRole) {
        this.role = userRole.name();
    }

    // Getter for the email
    public String getEmail() {
        return email;
    }

    // Setter for the email
    public void setEmail(String email) {
        this.email = email;
    }   
}