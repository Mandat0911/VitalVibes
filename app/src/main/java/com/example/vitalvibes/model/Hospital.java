package com.example.vitalvibes.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Hospital implements Serializable {
    private String hospitalId;
    private String name;
    private String address;
    private String hospitalBio;
    private String location; // location landmark
    private String mobile;
    private String site; // Website
    private float rating; // Rating of the hospital
    private String date; // Date (could be timestamp or formatted date)
    private ArrayList<String> pic;

    private String startDate;
    private String endDate;

    // Default constructor
    public Hospital() {}

    // Constructor with all fields

    public Hospital(String hospitalId, String name, String address, String hospitalBio, String location, String mobile, String site, Float rating, String date, ArrayList<String> pic, String startDate, String endDate) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.address = address;
        this.hospitalBio = hospitalBio;
        this.location = location;
        this.mobile = mobile;
        this.site = site;
        this.rating = rating;
        this.date = date;
        this.pic = pic;
        this.startDate = startDate;
        this.endDate = endDate;

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

    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
