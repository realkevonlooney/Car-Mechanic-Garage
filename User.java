package com.example;
public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    
    // Constants for user roles
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_MECHANIC = "MECHANIC";
    public static final String ROLE_MANAGER = "MANAGER";
    
    // Constructor
    public User(String userId, String username, String password, String firstName, 
                String lastName, String email, String phoneNumber, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
    
    // Authentication method
    public boolean authenticate(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }
    
    // Method to check user permissions
    public boolean hasPermission(String action) {
        return true;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    // Password should only be changed through a secure method
    protected void setPassword(String newPassword) {
        this.password = newPassword;
    }
}