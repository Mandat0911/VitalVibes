package com.example.vitalvibes.model;

import java.util.Date;

public class Donor {

    private String userId;
    private String name;
    private String email;
    private Date Dob;
    private String phoneNumber;
    private String password;
    private String role;

    public Donor() {
    }

    public Donor(String userId, String name, String email, Date dob, String phoneNumber, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.Dob = dob;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public Date getDob() {
        return Dob;
    }

    public void setDob(Date dob) {
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
}
