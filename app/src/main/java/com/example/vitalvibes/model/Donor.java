package com.example.vitalvibes.model;

public class Donor {

    private String userId;
    private String name;
    private String email;
    private String Dob;
    private String phoneNumber;
    private String password;
    private String bloodType;
    private String role;

    public Donor() {
    }

    public Donor(String userId, String name, String email, String dob, String phoneNumber, String password, String bloodType, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.Dob = dob;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.bloodType = bloodType;
        this.role = role;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
}
