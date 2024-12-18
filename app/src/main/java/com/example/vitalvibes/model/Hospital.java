package com.example.vitalvibes.model;

import com.google.android.gms.maps.model.LatLng; // for Google Maps LatLng

import java.util.ArrayList;

public class Hospital {
    private String hospitalId;
    private String name;
    private String address;
    private String hospitalBio;
    private String nearest; // Nearest landmark
    private String mobile;
    private String site; // Website
    //private LatLng location; // Latitude and Longitude for Google Maps
    private Double rating; // Rating of the hospital
    private String date; // Date (could be timestamp or formatted date)
    private ArrayList<String> pic;

    // Default constructor
    public Hospital() {}

    // Constructor with all fields


    public Hospital(String hospitalId, String name, String address, String hospitalBio, String nearest, String mobile, String site, Double rating, String date, ArrayList<String> pic) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.address = address;
        this.hospitalBio = hospitalBio;
        this.nearest = nearest;
        this.mobile = mobile;
        this.site = site;
        this.rating = rating;
        this.date = date;
        this.pic = pic;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<String> getPic() {
        return pic;
    }

    public void setPic(ArrayList<String> pic) {
        this.pic = pic;
    }
}
