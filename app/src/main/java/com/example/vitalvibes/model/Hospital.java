package com.example.vitalvibes.model;

import com.google.android.gms.maps.model.LatLng; // for Google Maps LatLng

public class Hospital {
    private String hospitalId;
    private String address;
    private String hospitalBio;
    private String picture; // Image URL or Base64 string
    private String nearest; // Nearest landmark
    private String mobile;
    private String site; // Website
    private LatLng location; // Latitude and Longitude for Google Maps
    private Double rating = 0.0; // Rating of the hospital
    private String date; // Date (could be timestamp or formatted date)

    // Default constructor
    public Hospital() {}

    // Constructor with all fields
    public Hospital(String hospitalId, String address, String hospitalBio, String picture,
                    String nearest, String mobile, String site, LatLng location,
                    Double rating, String date) {
        this.hospitalId = hospitalId;
        this.address = address;
        this.hospitalBio = hospitalBio;
        this.picture = picture;
        this.nearest = nearest;
        this.mobile = mobile;
        this.site = site;
        this.location = location;
        this.rating = rating;
        this.date = date;
    }

    // Getters and Setters
    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHospitalBio() {
        return hospitalBio;
    }

    public void setHospitalBio(String hospitalBio) {
        this.hospitalBio = hospitalBio;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNearest() {
        return nearest;
    }

    public void setNearest(String nearest) {
        this.nearest = nearest;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
