package com.example.vitalvibes.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Hospital implements Serializable {
    private String hospitalId;
    private String SiteName;
    private String address;
    private String hospitalBio;
    private String mobile;
    private ArrayList<String> pic;
    private String startDate;
    private String endDate;

    // Default constructor
    public Hospital() {}

    // Constructor with all fields

    public Hospital(String hospitalId, String siteName, String address, String hospitalBio, String mobile, ArrayList<String> pic, String startDate, String endDate) {
        this.hospitalId = hospitalId;
        SiteName = siteName;
        this.address = address;
        this.hospitalBio = hospitalBio;
        this.mobile = mobile;
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

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
